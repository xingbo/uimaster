//$Revision: 1.3 $
// baserule_Default
function bmiasia_ebos_constraint_baserule_Default() {
  if(this.ui.type == undefined) {
    if(this.ui[0] != undefined && this.ui[0].type == "radio") {
      for(var i = 0; i < this.ui.length; i++) {
        if(this.ui[i].value == this.value) {
          this.ui[i].checked == true;
        }
      }
    }
  }
  if(this.ui.type == "text" | this.ui.type == "password") {
    this.ui.value= this.value;
  }else if(this.ui.type == "select-one") {
    this.ui.value = this.value;
  }else if(this.ui.type == "checkbox") {
    this.ui.checked = true;
  }
  
  return true;
}
