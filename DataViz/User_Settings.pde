class User_Settings {
  boolean option_selected;
  boolean scatter_plot;
  
  int rectX, rectY, rectW, rectH;
  
  int rectUm;
  boolean Display() {
    textSize(40);
    fill(255);
    stroke(0);
    strokeWeight(5);
    rectX = int (displayWidth * .035);
    rectY = int (displayHeight * .11);   
    rectH = int (displayHeight * .035);
    
    rect(rectX, rectY, rectX, rectH);
    
    fill(0);
    if(option_selected) {
      text("2016", rectX * 1.05, rectY * 1.05, 150, 350);
      return true;
    } else {
      text("2020", rectX * 1.05, rectY * 1.05, 150, 350);
      return false;
    }
  }
  
  boolean mouse_over_box() {
    return (((150 < mouseX) && (300 > mouseX)) && ((200  < mouseY) && (500 > mouseY)));
  }
  
  void switch_year() {option_selected = !option_selected;}
  
  void switch_graph() {scatter_plot = !scatter_plot;}
  
  User_Settings() {
    option_selected = false;
    scatter_plot = true;
  }
}
