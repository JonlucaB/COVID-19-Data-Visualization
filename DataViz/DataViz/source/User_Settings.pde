class User_Settings {
  boolean option_selected;
  boolean scatter_plot;
  
  int rectX, rectY, rectW, rectH;
  
  int rectUm;
  boolean Display() {
    textSize(20);
    fill(255);
    stroke(0);
    strokeWeight(2);
    rectX = int (width * .04);
    rectY = int (height * .15);   
    rectH = int (height * .035);
    
    rect(rectX, rectY, rectX, rectH);
    
    fill(0);
    if(option_selected) {
      text("2016", rectX * 1.1, rectY * 1.05, rectX, rectH);
      return true;
    } else {
      text("2020", rectX * 1.1, rectY * 1.05, rectX, rectH);
      return false;
    }
  }
  
  boolean mouse_over_box() {
    return (((rectX < mouseX) && ((rectX * 2) > mouseX)) && (((rectY)  < mouseY) && ((rectY + rectH) > mouseY)));
  }
  
  void switch_year() {option_selected = !option_selected;}
  
  void switch_graph() {scatter_plot = !scatter_plot;}
  
  User_Settings() {
    option_selected = false;
    scatter_plot = true;
  }
}
