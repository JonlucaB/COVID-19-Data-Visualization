class State {
  String name;
  String file_Name, image_Name;
  int image_Length, image_Height;
  County[] counties;
  float lat_Start, lat_End, lon_Start, lon_End;
  PImage img;
  float max_Cases, max_Deaths, max_Population;
  int image_xx, image_Print_xx;
  
  int scatterX_StartX, scatterX_EndX, scatterX_Y, scatterY_X, scatterY_StartY, scatterY_EndY;
  
 State(String _name, String _file_Name, String _image_Name, int _image_Length, int _image_Height, float _lat_Start, float _lat_End, float _lon_Start, float _lon_End, float mcases, float mdeaths, float mpopulation) {
   name = _name;
   file_Name = _file_Name;
   image_Name = _image_Name;
   image_Height = _image_Height;
   image_Length = _image_Length;
   lat_Start = _lat_Start;
   lat_End = _lat_End;
   lon_Start = _lon_Start;
   lon_End = _lon_End;
   
   max_Cases = mcases; max_Deaths = mdeaths; max_Population = mpopulation;
   
   image_xx = ((image_Length * displayHeight)/image_Height);
   image_Print_xx = (displayWidth/2) - (image_xx/2);
   
   scatterX_StartX = int ((.01 * image_xx) + image_Print_xx);
   scatterX_Y = int (.93 * displayHeight);
   scatterX_EndX = int ((.98 * image_xx) + image_Print_xx);
   
   scatterY_X = int (.03 * image_xx) + image_Print_xx;
   scatterY_StartY = int (.95 * displayHeight);
   scatterY_EndY = int (.03 * displayHeight);
   
     county_data = loadTable(file_Name, "header");
    int county_index = 0; int rowCount = county_data.getRowCount(); //<>//
    counties = new County[rowCount];
    img = loadImage(image_Name);
    //println("debug");
    //println(county_data.getRowCount());
    for(TableRow row : county_data.rows()) {
     counties[county_index] = new County(row.getString("name"),
                                         row.getFloat("lat"),
                                         row.getFloat("long"),
                                         row.getFloat("cases"),
                                         row.getFloat("deaths"),
                                         row.getFloat("red_votes_2016"),
                                         row.getFloat("red_votes_2020"),
                                         row.getFloat("population"),
                                         lat_Start,
                                         lat_End,
                                         lon_Start,
                                         lon_End,
                                         image_Print_xx,
                                         image_Print_xx + image_xx);
     county_index += 1; 
   }
 }
 
 void display_Image() {
   background(255);
   image(img, image_Print_xx, 0, image_xx, displayHeight);
 }
 
 void display_Scatter() {
    background(255);
    //origin at (50, 50)
    stroke(0);
    strokeWeight(5);
    line(scatterX_StartX, scatterX_Y, scatterX_EndX, scatterX_Y);
    line(scatterY_X, scatterY_StartY, scatterY_X, scatterY_EndY);
    textSize(int (displayWidth * .01));
    fill(0);
    
    text("% Voted Republican", displayWidth/2, displayHeight * 96);
    text("% Cases / Population", scatterX_StartX, displayHeight * .014);
    //x vals
    for(int r = 0; r < 100; r += 5) {
      text(r, map(r, 0, 100, scatterY_X, scatterX_EndX) + (displayWidth * .0013), scatterX_Y * 1.02);
    }
    //y vals
    int compensate_for_Y = int (displayHeight * .01);
    for(int c = 0; c < 15; c += 5) {
      text(c, scatterY_X - (displayWidth * 0.015), map(c, 0, 10, scatterX_Y - compensate_for_Y, compensate_for_Y + scatterY_EndY));
    }
    
    text("% Voted Republican", displayWidth/2, displayHeight * .97);
    text("% Cases / Population", scatterX_StartX, displayHeight * .014);
    
    boolean year = settings.Display();
    
    for(County data_point : selected_State.counties) {
      if(data_point.pass_Filter(scaler.display(), scaler.filter, scaler.greater_than, year)) data_point.Scatter_Display(year, scatterY_X, scatterX_EndX, scatterX_Y, scatterY_EndY);
      if(mousePressed) mouse_clicked_circle(data_point, 2);
    } 
 }
}
