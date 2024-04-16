class Drag_Circle_Scale {
  float x,y,d;
  float offX;
  float offY;
  float left_Bound, right_Bound;
  float scale_Left, scale_Right, max_Cases, max_Deaths, max_Population;
  boolean dragging = false;
  boolean hovering = false;
  color regularColor = color(0);
  color highlightColor = color(128);
  boolean firstPress = false;
  boolean isPressed = false;
  
  int filter = 1;
  boolean greater_than = true;
  
  // _y y coordinate of 
  Drag_Circle_Scale(float _x, float _y, float _d, float left, float right, float sLeft, float sRight, float mcases, float mdeaths, float mpopulation) {
    x = left; y = _y; d = _d; left_Bound = left; right_Bound = right; scale_Left = sLeft; scale_Right = sRight; max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation; 
  }
  
  void reconfigure(float mcases, float mdeaths, float mpopulation) {
    max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation;
  }
  
  void flip_Greater_Than() {
   greater_than = !greater_than; 
  }
  
  void change_Filter(int choice) {
    if(choice == 1) {
        scale_Right = max_Cases;
      } else if (choice == 2) {
          scale_Right = max_Deaths;
      } else if (choice == 3) {
         scale_Right = 100;
      } else if (choice == 4) {
          scale_Right = max_Population;
      } else scale_Right = 0;
    
    filter = choice; 
  }
  
  float display() {
    fill(255);
    stroke(0);
    strokeWeight(2);
    rect(left_Bound - 20, y - 30, right_Bound - left_Bound + 40, d*4);
    line(left_Bound, y, right_Bound, y);
    
    fill(0);
    //If the mouse is not pressed, stop dragging
    
    
    
    if(!mousePressed) {
      dragging = false;
      firstPress = false;
      isPressed = false;
    }
    //If this is the first time the mouse has been pressed, mark
    //the firstPress event
    else {
      if(isPressed) {
        firstPress = false;
      } else {
        firstPress = true;
        isPressed = true;
      }
    }
    
    //If the item was already being dragged
    if(dragging) {
      x = mouseX + offX;
      if(x > right_Bound) x = right_Bound; else if(x < left_Bound) x = left_Bound; 
    } else { //Otherwise, item not already being dragged...
      //So, if the mouse is now over the item...
      //We used the dist() function in class, this way is faster because
      //it doesn't involve taking a square root, which is a pretty slow operation
      float diffX = x-mouseX;
      float diffY = y-mouseY;
      if(diffX*diffX+diffY*diffY < d*d/4) {
        //If this is the first press of the mouse, then start dragging
        if(firstPress) {
          dragging = true;
          offX = x - mouseX;
        } else { //otherwise, this is just a hover
          hovering = true;
        }
      }
      else { //If the mouse is not over the item, then don't hover
        hovering = false;
      }
    }
      
   fill(0);
   textSize(20);
   textAlign(LEFT);
   if(filter == 1) {
        text("Cases",left_Bound, y + d * 1.5);
      } else if (filter == 2) {
          text("Deaths",left_Bound, y + d * 1.5);
      } else if (filter == 3) {
          text("% Republican",left_Bound, y + d * 1.5);
      } else if (filter == 4) {
          text("Population",left_Bound, y + d * 1.5);
      } else text("No Filter", left_Bound, y + d * 1.5);
    
    if(greater_than) {
       text('>', left_Bound + 120, y + d * 1.55); 
    } else {
      text('<', left_Bound + 120, y + d * 1.55);
    }
    
    text(int (map(x, left_Bound, right_Bound, scale_Left, scale_Right)), left_Bound - 20, y - 50);
    
    noStroke();
    fill(0);
    ellipse(x,y,d,d);
    float ret = map(x, left_Bound, right_Bound, scale_Left, scale_Right);;
    if(filter == 3) ret /= 100;
    
    return ret;
  }
}
