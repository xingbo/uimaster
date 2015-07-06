
/**
 * 
 * @param {Object} message
 * @param {Object} distElemnt
 * @param {Object} position
 */
function bmiasia_ebos_appbase_MessageDialog(message, distElement, position)
{
	this.message = message;
	this.distElement = distElement;
	this.position = position;
	
	this.dialog = document.createElement("DIV");
	this.dialog.id = "messageDialogId"+this.getRandomId();
	this.dialog.className = "messagedialog";
}

bmiasia_ebos_appbase_MessageDialog.position = 
{
	TOP: 1,
	BOTTOM: 2,
	LEFT: 3,
	RIGHT: 4
};

bmiasia_ebos_appbase_MessageDialog.show = function(message, distElement, position)
{
	var dialog = new bmiasia_ebos_appbase_MessageDialog(message,distElement,position);
	dialog.paint();
	dialog.show();
};

bmiasia_ebos_appbase_MessageDialog.clearAll = function()
{
	var mDialogContainer = bmiasia_ebos_appbase_Container.getDefaultContainer(bmiasia_ebos_appbase_MessageDialog.prototype.CONTEXT_DIV_ID);
	while(mDialogContainer.childNodes.length > 0)
	{
		mDialogContainer.removeChild(mDialogContainer.childNodes.item(0));
	}
};

bmiasia_ebos_appbase_MessageDialog.prototype = new bmiasia_ebos_appbase_Component();
bmiasia_ebos_appbase_MessageDialog.prototype.CONTEXT_DIV_ID = "bmiasia_ebos_appbase_MessageDialog";
bmiasia_ebos_appbase_MessageDialog.prototype.updateMessage = function( message )
{
	this.message = message;
	this.dialog.innerHTML = this.message;
};
bmiasia_ebos_appbase_MessageDialog.prototype.paint = function()
{
	this.dialog.innerHTML = this.message;
	bmiasia_ebos_appbase_Container.getDefaultContainer(this.CONTEXT_DIV_ID).appendChild(this.dialog);
};
bmiasia_ebos_appbase_MessageDialog.prototype.show = function()
{
	var top = 0;
	var left = 0;
	var elmPosition = bmiasia_ebos_appbase_util_elmPosition(this.distElement);
	var thisPosition = bmiasia_ebos_appbase_util_elmPosition(this.dialog);
	if(bmiasia_ebos_appbase_MessageDialog.position.BOTTOM == this.position)
	{
		top = elmPosition.top + elmPosition.height + 5;
		left = elmPosition.left;
	}
	else if(bmiasia_ebos_appbase_MessageDialog.position.LEFT == this.position)
	{
		top = elmPosition.top;
		left = elmPosition.left - thisPosition.width - 5;
	}
	else if(bmiasia_ebos_appbase_MessageDialog.position.RIGHT == this.position)
	{
		top = elmPosition.top;
		left = elmPosition.left + elmPosition.width + 5;
	}
	else
	{
		//default TOP position.
		top = elmPosition.top - thisPosition.height - 5;
		left = elmPosition.left;
	}
	
	this.dialog.style.pixelTop  = top;
    this.dialog.style.pixelLeft = left;
	this.visible = true;
};
bmiasia_ebos_appbase_MessageDialog.prototype.close = function()
{
	this.dialog.style.display = "none";
	this.visible = false;
};
bmiasia_ebos_appbase_MessageDialog.prototype.destroy = function()
{
	this.visible = false;
	bmiasia_ebos_appbase_Container.getDefaultContainer(this.CONTEXT_DIV_ID).removeChild(this.dialog);
	this.dialog = null;
};
