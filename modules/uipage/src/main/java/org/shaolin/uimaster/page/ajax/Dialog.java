/*
* Copyright 2015 The UIMaster Project
*
* The UIMaster Project licenses this file to you under the Apache License,
* version 2.0 (the "License"); you may not use this file except in compliance
* with the License. You may obtain a copy of the License at:
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
* WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
* License for the specific language governing permissions and limitations
* under the License.
*/
package org.shaolin.uimaster.page.ajax;

import org.shaolin.uimaster.page.AjaxContext;
import org.shaolin.uimaster.page.AjaxActionHelper;
import org.shaolin.uimaster.page.IJSHandlerCollections;
import org.shaolin.uimaster.page.ajax.json.IDataItem;
import org.shaolin.uimaster.page.ajax.json.JSONObject;

/**
 * Please use JavaScript to instead of this feature due to it cannot hold the user thread for entering data.
 *
 * @deprecated
 */
public class Dialog extends Container
{
    private static final long serialVersionUID = 121214544768634345L;
    //OptionType
    public static final int YES_NO_OPTION = 0;
    public static final int YES_NO_CANCEL_OPTION = 1;
    public static final int OK_CANCEL_OPTION = 2;
    public static final int OK_ONLY_OPTION = 3;//for showMessageDialog

    //MessageType which decides the icon.
    public static final int ERROR_MESSAGE = 0;
    public static final int INFORMATION_MESSAGE = 1;
    public static final int WARNING_MESSAGE = 2;
    public static final int QUESTION_MESSAGE = 3;

    //ReturnType
    public static final int YES_OPTION = 0;//for showConfirmDialog
    public static final int NO_OPTION = 1;//for showConfirmDialog
    public static final int OK_OPTION = 2;//for showInputDialog, showMessageDialog, showOptionDialog
    public static final int CANCEL_OPTION = 3;//for all
    public static final int CLOSED_OPTION = 4;//for all

    //DialogType
    public static final int MESSAGE_TYPE = 0;
    public static final int INPUT_TYPE = 1;
    public static final int OPTION_TYPE = 2;
    public static final int CONFIRM_TYPE = 3;

    //Default parameters
    public static final String DEF_TITLE = "Dialog";
    public static final String DEF_MESSAGE = "Message";
    public static final int DEF_OPTION_TYPE = OK_CANCEL_OPTION;//OK and Cancel
    public static final String[] DEF_OPTIONS = null;
    public static final int DEF_INITIAL_VALUE = 0;
    public static final int DEF_DIALOG_TYPE = MESSAGE_TYPE;
    public static final int DEF_MESSAGE_TYPE = INFORMATION_MESSAGE;

    public static final int DEF_X = -1;
    public static final int DEF_Y = -1;

    private String title;
    private String message;
    private int optionType;
    private String[] options;
    private int initialValue;//0,1,2...
    private int messageType;
    private int dialogType;
    private int x = DEF_X;
    private int y = DEF_Y;
    private String frameInfo = "";

    public Dialog(String uiid)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.title = DEF_TITLE;
        this.message = DEF_MESSAGE;
        this.optionType = DEF_OPTION_TYPE;
        this.options = DEF_OPTIONS;
        this.initialValue = DEF_INITIAL_VALUE;
        this.messageType = DEF_MESSAGE_TYPE;
        this.dialogType = DEF_DIALOG_TYPE;
    }
    public Dialog(String uiid, String title, String message, int optionType, String[] options,
            int initialValue, int messageType, int dialogType)
    {
        super(AjaxActionHelper.getAjaxContext().getEntityPrefix() + uiid, new CellLayout());
        this.title = title;
        this.message = message;
        this.optionType = optionType;
        this.options = options;
        this.initialValue = initialValue;
        this.messageType = messageType;
        this.dialogType = dialogType;
    }
    public int getInitialValue()
    {
        return initialValue;
    }
    public void setInitialValue(int initialValue)
    {
        this.initialValue = initialValue;
    }
    public String getMessage()
    {
        return message;
    }
    public void setMessage(String message)
    {
        this.message = message;
    }
    public String[] getOptions()
    {
        return options;
    }
    public void setOptions(String[] options)
    {
        this.options = options;
    }
    public int getOptionType()
    {
        return optionType;
    }
    public void setOptionType(int optionType)
    {
        this.optionType = optionType;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public int getMessageType()
    {
        return messageType;
    }
    public void setMessageType(int messageType)
    {
        this.messageType = messageType;
    }
    public int getDialogType()
    {
        return dialogType;
    }
    public void setDialogType(int dialogType)
    {
        this.dialogType = dialogType;
    }
    public int getX() {
        return x;
    }
    public void setX(int x) {
        this.x = x;
    }
    public int getY() {
        return y;
    }
    public void setY(int y) {
        this.y = y;
    }
    public String getFrameInfo() {
        return frameInfo;
    }
    public void setFrameInfo(String frameInfo) {
        this.frameInfo = frameInfo;
    }
    /**
     * Brings up a dialog with the options Yes and No with the title
     * @param message
     * @param title
     * @param frameInfo
     * @return ReturnType
     */
    public static int showConfirmDialog(String message, String title, String frameInfo) throws InterruptedException
    {
        return showConfirmDialog(message, title, Dialog.DEF_X, Dialog.DEF_Y, frameInfo);
    }

    /**
     * Brings up a dialog with the options Yes and No with the title
     * @param message
     * @param title
     * @param x
     * @param y
     * @param frameInfo
     * @return
     * @throws InterruptedException
     */
    public static int showConfirmDialog(String message, String title, int x, int y, String frameInfo) throws InterruptedException
    {
        return showConfirmDialog(message, title, Dialog.DEF_OPTION_TYPE, Dialog.DEF_MESSAGE_TYPE, x, y, frameInfo);
    }

    /**
     * Brings up a dialog where the number of choices is determined by the optionType parameter.
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @param frameInfo
     * @return ReturnType
     * @throws InterruptedException
     */
    public static int showConfirmDialog(String message, String title, int optionType, int messageType, int x, int y, String frameInfo) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(CONFIRM_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setOptionType(optionType);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem item = createDataItem(dialog.getId(), jsCode);
        item.setFrameInfo(frameInfo);
        ajaxContext.addDataItem(item);
        //ajaxContext.suspend(dialog);

        //String returnTypeStr = ajaxContext.getRequest().getParameter("returnType");
        return -1;
    }

    /**
     * Prompts the user for input in a blocking dialog.
     * @param message
     * @param title
     * @param messageType
     * @param frameInfo
     * @return client's input String if client clicks "Ok" option, else return null
     * @throws InterruptedException
     */
    public static String showInputDialog(String message, String title, int messageType, String frameInfo) throws InterruptedException
    {
        return showInputDialog(message,title,messageType,Dialog.DEF_X,Dialog.DEF_Y, frameInfo);
    }

    /**
     * Prompts the user for input in a blocking dialog.
     *
     * @param message
     * @param title
     * @param messageType
     * @param x
     * @param y
     * @param frameInfo
     * @return
     * @throws InterruptedException
     */
    public static String showInputDialog(String message, String title, int messageType, int x, int y, String frameInfo) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(INPUT_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem item = createDataItem(dialog.getId(), jsCode);
        item.setFrameInfo(frameInfo);
        ajaxContext.addDataItem(item);
        return "";
    }

    /**
     * Brings up a dialog displaying a message.
     * @param message
     * @param title
     * @param messageType
     * @param frameInfo
     * @throws InterruptedException
     */
    public static void showMessageDialog(String message, String title, int messageType, String frameInfo) throws InterruptedException
    {
        showMessageDialog(message,title,messageType,Dialog.DEF_X,Dialog.DEF_Y, frameInfo);
    }

    /**
     * Brings up a dialog displaying a message.
     *
     * @param message
     * @param title
     * @param messageType
     * @param x
     * @param y
     * @param frameInfo
     * @throws InterruptedException
     */
    public static void showMessageDialog(String message, String title, int messageType,int x, int y, String frameInfo) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(MESSAGE_TYPE);
        dialog.setOptionType(OK_ONLY_OPTION);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem item = createDataItem(dialog.getId(), jsCode);
        item.setFrameInfo(frameInfo);
        ajaxContext.addDataItem(item);
        //ajaxContext.suspend(dialog);

        return;
    }

    /**
     * Brings up a dialog where the initial choice is determined by the initialValue parameter
     * and the number of choices is determined by the optionType parameter.
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @param options
     * @param initialValue determine the initial choice of options
     * @param frameInfo
     * @return return the selection from client if clicks "Ok" option,
     *          else return -1
     * @throws InterruptedException
     */
    public static int showOptionDialog(String message, String title, int optionType,
            int messageType, String[] options, int initialValue, String frameInfo) throws InterruptedException
    {
        return showOptionDialog(message,title,optionType,messageType,options,initialValue,Dialog.DEF_X,Dialog.DEF_Y,frameInfo);
    }

    /**
     * Brings up a dialog where the initial choice is determined by the initialValue parameter
     * and the number of choices is determined by the optionType parameter.
     *
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @param options
     * @param initialValue
     * @param x
     * @param y
     * @return
     * @throws InterruptedException
     */
    public static int showOptionDialog(String message, String title, int optionType,
            int messageType, String[] options, int initialValue,int x, int y, String frameInfo) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(OPTION_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setOptionType(optionType);
        dialog.setMessageType(messageType);
        dialog.setOptions(options);
        dialog.setInitialValue(initialValue);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        IDataItem item = createDataItem(dialog.getId(), jsCode);
        item.setFrameInfo(frameInfo);
        ajaxContext.addDataItem(item);
        //ajaxContext.suspend(dialog);
        /*
        String returnTypeStr = ajaxContext.getRequest().getParameter("returnType");
        int returnType = Integer.valueOf(returnTypeStr).intValue();
        if(returnType == OK_OPTION)
        {
            String returnResultStr = ajaxContext.getRequest().getParameter("returnResult");
            return Integer.valueOf(returnResultStr).intValue();
        }
        else
        {
            return -1;
        }
        */
        return -1;
    }

    /**
     * Brings up a dialog with the options Yes and No with the title
     * @param message
     * @param title
     * @return ReturnType
     */
    public static int showConfirmDialog(String message, String title) throws InterruptedException
    {
        return showConfirmDialog(message, title, Dialog.DEF_X, Dialog.DEF_Y);
    }

    /**
     * Brings up a dialog with the options Yes and No with the title
     * @param message
     * @param title
     * @param x
     * @param y
     * @return
     * @throws InterruptedException
     */
    public static int showConfirmDialog(String message, String title, int x, int y) throws InterruptedException
    {
        return showConfirmDialog(message, title, Dialog.DEF_OPTION_TYPE, Dialog.DEF_MESSAGE_TYPE, x, y);
    }

    /**
     * Brings up a dialog where the number of choices is determined by the optionType parameter.
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @return ReturnType
     * @throws InterruptedException
     */
    public static int showConfirmDialog(String message, String title, int optionType, int messageType, int x, int y) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(CONFIRM_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setOptionType(optionType);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        ajaxContext.addDataItem(createDataItem(dialog.getId(), jsCode));
        //ajaxContext.suspend(dialog);
        /*
        String returnTypeStr = ajaxContext.getRequest().getParameter("returnType");
        return Integer.valueOf(returnTypeStr).intValue();
        */
        return -1;
    }

    /**
     * Prompts the user for input in a blocking dialog.
     * @param message
     * @param title
     * @param messageType
     * @return client's input String if client clicks "Ok" option, else return null
     * @throws InterruptedException
     */
    public static String showInputDialog(String message, String title, int messageType) throws InterruptedException
    {
        return showInputDialog(message,title,messageType,Dialog.DEF_X,Dialog.DEF_Y);
    }

    /**
     * Prompts the user for input in a blocking dialog.
     *
     * @param message
     * @param title
     * @param messageType
     * @param x
     * @param y
     * @return
     * @throws InterruptedException
     */
    public static String showInputDialog(String message, String title, int messageType, int x, int y) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(INPUT_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        ajaxContext.addDataItem(createDataItem(dialog.getId(), jsCode));
        //ajaxContext.suspend(dialog);
        /*
        String returnTypeStr = ajaxContext.getRequest().getParameter("returnType");
        int returnType = Integer.valueOf(returnTypeStr).intValue();
        if(returnType == OK_OPTION)
        {
            String returnResultStr = ajaxContext.getRequest().getParameter("returnResult");
            return returnResultStr;
        }
        else
        {
            return null;
        }
        */
        return "";
    }

    /**
     * Brings up a dialog displaying a message.
     * @param message
     * @param title
     * @param messageType
     * @throws InterruptedException
     */
    public static void showMessageDialog(String message, String title, int messageType) throws InterruptedException
    {
        showMessageDialog(message,title,messageType,Dialog.DEF_X,Dialog.DEF_Y);
    }

    /**
     * Brings up a dialog displaying a message.
     *
     * @param message
     * @param title
     * @param messageType
     * @param x
     * @param y
     * @throws InterruptedException
     */
    public static void showMessageDialog(String message, String title, int messageType,int x, int y) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(MESSAGE_TYPE);
        dialog.setOptionType(OK_ONLY_OPTION);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setMessageType(messageType);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        ajaxContext.addDataItem(createDataItem(dialog.getId(), jsCode));
        //ajaxContext.suspend(dialog);

        return;
    }

    /**
     * Brings up a dialog where the initial choice is determined by the initialValue parameter
     * and the number of choices is determined by the optionType parameter.
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @param options
     * @param initialValue determine the initial choice of options
     * @return return the selection from client if clicks "Ok" option,
     *          else return -1
     * @throws InterruptedException
     */
    public static int showOptionDialog(String message, String title, int optionType,
            int messageType, String[] options, int initialValue) throws InterruptedException
    {
        return showOptionDialog(message,title,optionType,messageType,options,initialValue,Dialog.DEF_X,Dialog.DEF_Y);
    }

    /**
     * Brings up a dialog where the initial choice is determined by the initialValue parameter
     * and the number of choices is determined by the optionType parameter.
     *
     * @param message
     * @param title
     * @param optionType
     * @param messageType
     * @param options
     * @param initialValue
     * @param x
     * @param y
     * @return
     * @throws InterruptedException
     */
    public static int showOptionDialog(String message, String title, int optionType,
            int messageType, String[] options, int initialValue,int x, int y) throws InterruptedException
    {
        Dialog dialog = new Dialog("dialog");
        dialog.setDialogType(OPTION_TYPE);
        dialog.setMessage(message);
        dialog.setTitle(title);
        dialog.setOptionType(optionType);
        dialog.setMessageType(messageType);
        dialog.setOptions(options);
        dialog.setInitialValue(initialValue);
        dialog.setX(x);
        dialog.setY(y);

        JSONObject jsono = new JSONObject(dialog);
        String jsCode = jsono.toString();

        AjaxContext ajaxContext = AjaxActionHelper.getAjaxContext();
        ajaxContext.addDataItem(createDataItem(dialog.getId(), jsCode));
        //ajaxContext.suspend(dialog);
        /*
        String returnTypeStr = ajaxContext.getRequest().getParameter("returnType");
        int returnType = Integer.valueOf(returnTypeStr).intValue();
        if(returnType == OK_OPTION)
        {
            String returnResultStr = ajaxContext.getRequest().getParameter("returnResult");
            return Integer.valueOf(returnResultStr).intValue();
        }
        else
        {
            return -1;
        }
        */
        return -1;
    }

    private static IDataItem createDataItem(String uiid, String data)
    {
        IDataItem dataItem = AjaxActionHelper.createDataItem();
        dataItem.setJsHandler(IJSHandlerCollections.OPEN_DIALOG);
        dataItem.setData(data);
        return dataItem;
    }

}

