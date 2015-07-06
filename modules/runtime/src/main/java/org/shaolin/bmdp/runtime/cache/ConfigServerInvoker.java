package org.shaolin.bmdp.runtime.cache;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.shaolin.bmdp.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigServerInvoker {
	
	private static final Logger logger = LoggerFactory.getLogger(ConfigServerInvoker.class);
	
	private boolean running = true;
	
	private boolean handshakeFinished = false;
	
	private final SocketChannel channel;
	
	private final Selector selector;
	
    // remote communication factors.
    public static final int SERVER_READY = 200;
    public static final int REQUEST = 1;
    public static final int ACCEPT = 2;
    public static final int REJECT = 3;
    public static final int BUSY = 4;
    public static final int HAS_NEXT = 5;
    public static final int FINISH = 6;

    private static final List<ResultHandler> handlers = new ArrayList<ResultHandler>();
    
	public ConfigServerInvoker(ResultHandler handler, String serverIp, int port) throws IOException {
		handlers.add(handler);
		
		channel = SocketChannel.open();

		channel.configureBlocking(false);
		selector = Selector.open();

		channel.connect(new InetSocketAddress(serverIp, port));
		channel.register(selector, SelectionKey.OP_CONNECT);
	}
	
	public static void addHandler(ResultHandler handler) {
		handlers.add(handler);
	}

	public void listen() throws IOException {
		logger.info("trying to connect the config server {}..." + channel.toString());
		while (running) {
			selector.select();
			Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
			while (ite.hasNext()) {
				SelectionKey key = ite.next();
				ite.remove();
				if (key.isConnectable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					if (channel.isConnectionPending()) {
						channel.finishConnect();
					}
					channel.configureBlocking(false);
					sendSignal(channel, ConfigServerInvoker.REQUEST);
					channel.register(selector, SelectionKey.OP_READ);
				} else if (key.isReadable()) {
					SocketChannel channel = (SocketChannel) key.channel();
					if (handshakeFinished) {
						if (checkHasNext(channel)) {
							try {
								Serializable result = receiveData(channel);
								for (ResultHandler h : handlers) {
									h.handle(result);
								}
							} catch (ClassNotFoundException e) {
								logger.error("Read config server error: " + e.getMessage(),	e);
							}
						} else {
							sendSignal(channel, ConfigServerInvoker.FINISH);
							logger.info("The data transferring is finished.");
							this.running = false;
							break;
						}
					} else {
						int signal = receiveSignal(channel);
						while (signal == ConfigServerInvoker.BUSY) {
							//wait 3 seconds and request again.
							try {
								logger.info("The config server is busy, will retry 3 seconds later.");
								Thread.sleep(3000);
								signal = receiveSignal(channel);
							} catch (InterruptedException e) {
							}
						} 
						if (signal == ConfigServerInvoker.ACCEPT) {
							handshakeFinished = true;
							logger.info("The connection is established to the config server.");
						} else if (signal == ConfigServerInvoker.REJECT) {
							handshakeFinished = true;
							logger.info("The connection is rejected by the config server.");
							try {
								channel.close();
							} catch (IOException e1) {
							}
							try {
								selector.close();
							} catch (IOException e) {
							}
						}
					}
				} else if (!key.isAcceptable()) {
						logger.info("The connection is disconnected.");
				}
			}
		}
		this.stop();
	}

	private void sendSignal(SocketChannel socketChannel, int signal)
			throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		buffer.putInt(signal);
		buffer.flip();
		socketChannel.write(buffer);
	}

	private int receiveSignal(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		while (buffer.remaining() > 0) {
			socketChannel.read(buffer);
		}
		buffer.flip();
		return buffer.getInt();
	}

	private boolean checkHasNext(SocketChannel socketChannel) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4);
		while (buffer.remaining() > 0) {
			socketChannel.read(buffer);
		}
		buffer.flip();
		return buffer.getInt() == ConfigServerInvoker.HAS_NEXT;
	}
	
	private Serializable receiveData(SocketChannel socketChannel)
			throws IOException, ClassNotFoundException {
		ByteBuffer dlBuffer = ByteBuffer.allocate(4);
		while (dlBuffer.remaining() > 0) {
			socketChannel.read(dlBuffer);
		}
		dlBuffer.flip();
		int dataLength = dlBuffer.getInt();

		ByteBuffer buffer = ByteBuffer.allocate(dataLength);
		while (buffer.remaining() > 0) {
			socketChannel.read(buffer);
		}
		buffer.flip();
		return SerializeUtil.readData(buffer.array());
	}
    
	public void stop() {
		running = false;
		
		handlers.clear();
		try {
			channel.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			selector.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public interface ResultHandler {
		void handle(Serializable result);
	}
	
}
