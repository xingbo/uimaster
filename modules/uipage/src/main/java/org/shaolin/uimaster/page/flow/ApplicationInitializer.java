package org.shaolin.uimaster.page.flow;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;

import org.shaolin.bmdp.runtime.AppContext;
import org.shaolin.bmdp.runtime.Registry;
import org.shaolin.bmdp.runtime.cache.ConfigServerInvoker;
import org.shaolin.bmdp.runtime.ce.ConstantServiceImpl;
import org.shaolin.bmdp.runtime.internal.AppServiceManagerImpl;
import org.shaolin.bmdp.runtime.spi.IAppServiceManager;
import org.shaolin.bmdp.runtime.spi.IEntityManager;
import org.shaolin.bmdp.runtime.spi.IServerServiceManager;
import org.shaolin.javacc.StatementParser;
import org.shaolin.javacc.context.OOEEContext;
import org.shaolin.javacc.context.OOEEContextFactory;
import org.shaolin.javacc.statement.CompilationUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationInitializer {

	private static Logger logger = LoggerFactory.getLogger(ApplicationInitializer.class);
	
	private final Map<String, byte[]> builtCacheObject = new HashMap<String, byte[]>();
	
	private byte[] registryBytes = null;
	
	private NioServer nioServer;
	
	private int port = 12301;
	
	private final String appName;
	
	public ApplicationInitializer(String appName) {
		this.appName = appName;
	}
	
	public void start(ServletContext servletContext) {
		logger.info("Initializing application instance " + appName + "...");
		try {
			builtCacheObject.clear();
			
			AppServiceManagerImpl appServiceManager = new AppServiceManagerImpl(appName, servletContext.getClassLoader());
			AppContext.register(appServiceManager);
			// add application to the server manager.
			IServerServiceManager.INSTANCE.addApplication(appName, appServiceManager);
			// bind the app context with the servlet context.
			servletContext.setAttribute(IAppServiceManager.class.getCanonicalName(), appServiceManager);
			
			IEntityManager entityManager = appServiceManager.getEntityManager();
			MasterInstanceListener.addEntityListeners(entityManager);
			//TODO: load all customized entities from the application folder.
			//entityManager.reloadDir(path);
			//load all customized constant items from DB table.
			entityManager.addEventListener((ConstantServiceImpl)appServiceManager.getConstantService());
			//load all customized workflow from DB table in WorkflowLifecycleServiceImpl.
			
			
	    	// wire all services.
	    	OOEEContext context = OOEEContextFactory.createOOEEContext();
	    	List<String> serviceNodes = Registry.getInstance().getNodeChildren("/System/services");
        	for (String path: serviceNodes) {
        		String expression = Registry.getInstance().getExpression("/System/services/" + path);
        		logger.debug("Evaluate module initial expression: " + expression);
        		CompilationUnit compliedUnit = StatementParser.parse(expression, context);
        		compliedUnit.execute(context);
        		
        	}
        	appServiceManager.startLifeCycleProviders();
        	logger.info("VogERP Server is ready for requesting.");
	    	
        	/**
			nioServer = new NioServer(port);
			logger.info("Config server is listening on " + port);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						nioServer.listen();
					} catch (IOException e) {
						logger.error("Fails to start Config server start! Error: "
										+ e.getMessage(), e);
					}
				}
			}, "uimaster-config-server").start();
			*/
		} catch (Throwable e) {
			logger.error("Fails to start Config server start! Error: " + e.getMessage(), e);
		}
	}
	
	public void stop(ServletContext servletContext) {
		logger.info("Stop Config server");
		AppServiceManagerImpl appServiceManager = (AppServiceManagerImpl)
				servletContext.getAttribute(IAppServiceManager.class.getCanonicalName());
		appServiceManager.stopLifeCycleProviders();
		
		IServerServiceManager.INSTANCE.removeApplication(appName);
		
		builtCacheObject.clear();
		//nioServer.stop();
	}
	
	public void updateCache() {
		//TODO: re-generatie all UI relevant such js, css.
		//1. broadcast the update to all application node.
		//2. the application ask for updating.
	}
	
	private class NioServer {
		
	    private final Selector selector;
	    private final ServerSocketChannel serverChannel;
	    
		public NioServer(int port) throws IOException {
			// Create a new server socket and set to non blocking mode  
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
			
			// Bind the server socket to the local host and port  
			serverChannel.socket().bind(new InetSocketAddress(port));
			selector = Selector.open();
			
			// Register accepts on the server socket with the selector. This  
            // step tells the selector that the socket wants to be put on the  
            // ready list when accept operations occur, so allowing multiplexed  
            // non-blocking I/O to take place.  
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		}
	    
		public void listen() throws IOException {
			// Here's where everything happens. The select method will  
            // return when any operations registered above have occurred, the  
            // thread has been interrupted, etc.  
			while (selector.select() > 0) {
				Iterator<SelectionKey> ite = selector.selectedKeys().iterator();

				while (ite.hasNext()) {
					SelectionKey key = ite.next();
					ite.remove();
					
					// The key indexes into the selector so you  
                    // can retrieve the socket that's ready for I/O  
					if (key.isAcceptable()) {
						ServerSocketChannel server = (ServerSocketChannel) key
								.channel();
						SocketChannel channel = server.accept();
						channel.configureBlocking(false);
						channel.register(selector, SelectionKey.OP_READ);
					} else if (key.isReadable()) {
						SocketChannel channel = (SocketChannel) key.channel();
						if (!channel.isOpen()) {
							System.out.println("the remote channel is closed!");
							continue;
						}
						int signal = receiveSignal(channel);
						if (signal == ConfigServerInvoker.REQUEST) {
							sendSignal(channel, ConfigServerInvoker.ACCEPT);
							logger.info("Accepted the client connection request {}.", key.channel().toString());
							
							logger.info("Writing registry to client: {}", key.channel().toString());
							sendData(channel, registryBytes);
							
							Set<Entry<String, byte[]>> cacheObjects = builtCacheObject
									.entrySet();
							for (Entry<String, byte[]> object : cacheObjects) {
								logger.info("Writing cache: {} to client: {}", 
										new Object[] {object.getKey(), key.channel().toString()});
								sendData(channel, object.getValue());
							}
							
							sendSignal(channel, ConfigServerInvoker.FINISH);
							channel.close();// close this client connected channel.
						} else if (signal == ConfigServerInvoker.FINISH) {
							continue;
						} else {
							logger.warn("Unsupported signal code {}", signal);
						}
					} else if (!key.isAcceptable()) {
						logger.info("The client connection is disconnected.");
					}
				}
			}
		}
	    
		public void stop() {
			try {
				serverChannel.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				selector.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private void sendSignal(SocketChannel socketChannel, int signal)
				throws IOException {
			ByteBuffer buffer = ByteBuffer.allocate(4);
			buffer.putInt(signal);
			buffer.flip();
			socketChannel.write(buffer);
		}

		private int receiveSignal(SocketChannel socketChannel)
				throws IOException {
			ByteBuffer buffer = ByteBuffer.allocate(4);
			while (buffer.remaining() > 0) {
				socketChannel.read(buffer);
			}
			buffer.flip();
			return buffer.getInt();
		}

		private void sendData(SocketChannel socketChannel, byte[] bytes)
				throws IOException {
			sendSignal(socketChannel, ConfigServerInvoker.HAS_NEXT);
			
			int dataLength = bytes.length;
			ByteBuffer length = ByteBuffer.allocate(4);
			length.putInt(dataLength);
			length.flip();
			socketChannel.write(length);

			ByteBuffer content = ByteBuffer.wrap(bytes);
			socketChannel.write(content);
		}
	}
	
}
