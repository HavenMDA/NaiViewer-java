/*
    NaiViewer: a cellular automata simulator.
    Copyright (C) 2026 MDA

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

import java.awt.*;

import java.awt.datatransfer.*;

import java.awt.event.*;

import java.util.Random;

class Funcs extends Frame {
	
	int statenum = 2;
	
	int[] bconds, sconds, dconds, fconds, kconds, lconds;
	
	int[] cursor;
	
	int[][] region;
	
	int[][] world;
	
	boolean tag = true;
	
	boolean color = false;
	
	boolean naive = false;
	
	boolean selecting = false;
	
	boolean carrying = true;
	
	String rulestring = "";
	
	enum Mode {
	    
	    Running, SettingRule;
	    
	}
	
	Mode mode = Mode.Running;
	
	enum Ruletype {
		
		INT, Generations, Deficient, BSFKL;
		
	}
	
	Ruletype ruletype = Ruletype.INT;
	
	public Funcs() {
		
		bconds = new int[117];
		
		sconds = new int[117];
		
		dconds = new int[117];
		
		fconds = new int[117];
		
		kconds = new int[117];
		
		lconds = new int[117];
		
		region = new int[2][2];
		
		world = new int[200][200];
		
		cursor = new int[2];
		
		cursor[0] = 100;
		
		cursor[1] = 100;
		
		region[0][0] = 100;
		
		region[0][1] = 100;
		
		region[1][0] = 100;
		
		region[1][1] = 100;
		
		addWindowListener(new WindowAdapter() {
			
			public void windowClosing(WindowEvent we) {
				
				System.exit(0);
				
			}
			
		});
		
		addKeyListener(new KeyAdapter() {
			
			public void keyPressed(KeyEvent ke) {
				
				int key = ke.getKeyCode();
				
				if (mode == Mode.Running) {
						    
					int minx = (region[0][0] < region[1][0]) ? region[0][0] : region[1][0];
					
					int maxx = (region[0][0] > region[1][0]) ? region[0][0] : region[1][0];
					
					int miny = (region[0][1] < region[1][1]) ? region[0][1] : region[1][1];
					
					int maxy = (region[0][1] > region[1][1]) ? region[0][1] : region[1][1];
					
					String pattern;
					
					Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
					
					switch (key) {
						
						case KeyEvent.VK_C:
						    
						    pattern = "";
						    
						    if ((minx == maxx) || (miny == maxy)) {
							    
							    for (int x = 0; x < 200; x++) {
							        
							        for (int y = 0; y < 200; y++) {
							               
							            pattern += world[x][y];
							            
							            pattern += " ";
							            
							        }
							        
							        pattern += "\n";
							        
							    }
							    
							} else {
								
							    for (int x = minx; x < maxx; x++) {
							        
							        for (int y = miny; y < maxy; y++) {
							               
							            pattern += world[x][y];
							            
							            pattern += " ";
							            
							        }
							        
							        pattern += "\n";
							        
							    }
								
							}
							
						    StringSelection selection = new StringSelection(pattern);
						    
						    clipboard.setContents(selection, null);
						    
						    break;
							
						case KeyEvent.VK_I:
						    
						    if ((minx == maxx) || (miny == maxy)) {
							    
							    for (int x = 0; x < 200; x++) {
							        
							        for (int y = 0; y < 200; y++) {
							               
							            world[x][y] = (world[x][y] == 0) ? 1 : 0;
							            
							        }
							        
							    }
							    
							} else {
								
							    for (int x = minx; x < maxx; x++) {
							        
							        for (int y = miny; y < maxy; y++) {
							               
							            world[x][y] = (world[x][y] == 0) ? 1 : 0;
							            
							        }
							        
							    }
								
							}
							
						    repaint();
						    
						    break;
							
						case KeyEvent.VK_K:
							
							if (color) {
								
								color = false;
								
								for (int x = 0; x < 200; x++) {
									
									for (int y = 0; y < 200; y++) {
										
										if (world[x][y] >= statenum) world[x][y] = statenum - 1;
										
									}
									
								}
								
							}
							
							else color = true;
							
							break;
							
						case KeyEvent.VK_R:
						    
						    int c = 2;
						    
						    Random random = new Random();
						    
						    if ((ruletype == Ruletype.Deficient) || (ruletype == Ruletype.BSFKL)) c = 3;
						    
						    if ((minx == maxx) || (miny == maxy)) {
							    
							    for (int x = 0; x < 200; x++) {
							        
							        for (int y = 0; y < 200; y++) {
							               
							            world[x][y] = random.nextInt(c);
							            
							        }
							        
							    }
							    
							} else {
								
							    for (int x = minx; x < maxx; x++) {
							        
							        for (int y = miny; y < maxy; y++) {
							               
							            world[x][y] = random.nextInt(c);
							            
							        }
							        
							    }
								
							}
							
						    repaint();
						    
						    break;
						    
						case KeyEvent.VK_T:
							
							mode = Mode.SettingRule;
							
							tag = true;
							
							break;
							
						case KeyEvent.VK_V:
							
							int cx = cursor[1], cy = cursor[0];
							
							int state = 0;
							
							try {
								
								if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
									
									pattern = (String) clipboard.getData(DataFlavor.stringFlavor);
									
									for (int n = 0; n < pattern.length(); n++) {
										
										char cc = pattern.charAt(n);
										
										switch (cc) {
											
											case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
												
												state *= 10;
												
												state += (cc - '0');
												
											}
											
											case ' ' -> {
												
												if (color) world[cy][cx] = (state == 0) ? 0 : 1;
												
												else world[cy][cx] = (state >= statenum) ? statenum - 1 : state;
												
												state = 0;
												
												cx++;
												
												cx %= 200;
												
											}
											
											case '\n' -> {
												
												cy++;
												
												cy %= 200;
												
												cx = cursor[1];
												
											}
											
										}
										
									}
									
								}
								
							} catch (Exception e) {
								
							}
							
							repaint();
							
							break;
							
						case KeyEvent.VK_0:
							
							selecting = false;
							
							carrying = true;
							
							region[0][0] = cursor[0];
							
							region[0][1] = cursor[1];
							
							region[1][0] = cursor[0];
							
							region[1][1] = cursor[1];
							
							repaint();
							
							break;
							
						case KeyEvent.VK_UP:
							
							cursor[0] += 199;
						    
						    cursor[0] %= 200;
						    
						    if (selecting) {
						    		
						    		region[1][0] = cursor[0];
						    		
						    		region[1][1] = cursor[1];
						    		
						    	} else {
						    		
						    		if (carrying) {
						    			
						    			region[0][0] = cursor[0];
						    			
						    			region[0][1] = cursor[1];
						    			
						    			region[1][0] = cursor[0];
						    			
						    			region[1][1] = cursor[1];
									
								}
								
							}
							
						    repaint();
							
							break;
							
						case KeyEvent.VK_DOWN:
							
							cursor[0] += 1;
						    
						    cursor[0] %= 200;
						    
						    if (selecting) {
						    		
						    		region[1][0] = cursor[0];
						    		
						    		region[1][1] = cursor[1];
						    		
						    	} else {
						    		
						    		if (carrying) {
						    			
						    			region[0][0] = cursor[0];
						    			
						    			region[0][1] = cursor[1];
						    			
						    			region[1][0] = cursor[0];
						    			
						    			region[1][1] = cursor[1];
									
								}
								
							}
						    
						    repaint();
							
							break;
							
						case KeyEvent.VK_LEFT:
							
							cursor[1] += 199;
						    
						    cursor[1] %= 200;
						    
						    if (selecting) {
						    		
						    		region[1][0] = cursor[0];
						    		
						    		region[1][1] = cursor[1];
						    		
						    	} else {
						    		
						    		if (carrying) {
						    			
						    			region[0][0] = cursor[0];
						    			
						    			region[0][1] = cursor[1];
						    			
						    			region[1][0] = cursor[0];
						    			
						    			region[1][1] = cursor[1];
									
								}
								
							}
						    
						    repaint();
							
							break;
							
						case KeyEvent.VK_RIGHT:
							
							cursor[1] += 1;
						    
						    cursor[1] %= 200;
						    
						    if (selecting) {
						    		
						    		region[1][0] = cursor[0];
						    		
						    		region[1][1] = cursor[1];
						    		
						    	} else {
						    		
						    		if (carrying) {
						    			
						    			region[0][0] = cursor[0];
						    			
						    			region[0][1] = cursor[1];
						    			
						    			region[1][0] = cursor[0];
						    			
						    			region[1][1] = cursor[1];
									
								}
								
							}
						    
						    repaint();
							
							break;
							
						case KeyEvent.VK_SPACE:
						    
						    advance();
						    
						    if ((!naive) && (bconds[0] != 0) && (sconds[104] == 0)) { // Prevents strobing in B0 rules
						    		
						    		if ((ruletype == Ruletype.INT) || (ruletype == Ruletype.Deficient)) advance();
						    		
						    		if (ruletype == Ruletype.Generations) for (int n = 1; n < statenum; n++) advance();
						    		
						    	}
						    	
						    	if ((!naive) && (ruletype == Ruletype.BSFKL) && (bconds[0] != 0) && (fconds[0] != 0) && (kconds[0] != 0)) {
						    		
						    		advance();
						    		
						    	}
						    	
						    	if ((!naive) && (ruletype == Ruletype.BSFKL) && (bconds[0] != 0) && (sconds[104] == 0) && (fconds[0] != 0) && (kconds[0] == 0) && (lconds[0] != 0)) {
						    		
						    		advance();
						    		
						    		advance();
						    		
						    	}
						    	
						    repaint();
						    
						    break;
						    
						case KeyEvent.VK_BACK_SPACE:
						    
						    if ((minx == maxx) || (miny == maxy)) {
							    
							    world = new int[200][200];
							    
							} else {
								
							    for (int x = minx; x < maxx; x++) {
							        
							        for (int y = miny; y < maxy; y++) {
							               
							            world[x][y] = 0;
							            
							        }
							        
							    }
								
							}
						    
						    repaint();
						    
						    break;
						    
						case KeyEvent.VK_ENTER:
						    
						    world[cursor[0]][cursor[1]]++;
						    
						    world[cursor[0]][cursor[1]] %= statenum;
						    
						    // repaint(cursor[0] * 4, cursor[1] * 4, 4, 4);
						    
						    repaint();
						    
						    break;
						    
						case KeyEvent.VK_SHIFT:
							
							if (selecting) {
								
								selecting = false;
								
								carrying = false;
								
							}
							
							else selecting = true;
							
							break;
							
					}
					
				}
				
				if (mode == Mode.SettingRule) {
					
					switch (key) {
						
						case KeyEvent.VK_ENTER:
							
							bconds = new int[117];
							
							sconds = new int[117];
							
							dconds = new int[117];
							
							fconds = new int[117];
							
							kconds = new int[117];
							
							lconds = new int[117];
							
							if (rulestring.equals("R")) {
								
								int type;
								
								Random random = new Random();
								
								if (random.nextInt(2) == 0) naive = false;
								
								else naive = true;
								
								type = random.nextInt(4);
								
								switch (type) {
									
									case 0 -> {
										
										ruletype = Ruletype.INT;
										
										for (int n = 0; n < 117; n++) {
											
											bconds[n] = random.nextInt(2);
											
											sconds[n] = random.nextInt(2);
											
										}
										
									}
									
									case 1 -> {
										
										ruletype = Ruletype.Generations;
										
										statenum = random.nextInt(9) + 2;
										
										for (int n = 0; n < 117; n++) {
											
											bconds[n] = random.nextInt(2);
											
											sconds[n] = random.nextInt(2);
											
										}
										
									}
									
									case 2 -> {
										
										ruletype = Ruletype.Deficient;
										
										for (int n = 0; n < 117; n++) {
											
											bconds[n] = random.nextInt(2);
											
											sconds[n] = random.nextInt(2);
											
											dconds[n] = random.nextInt(2);
											
										}
										
									}
									
									case 3 -> {
										
										ruletype = Ruletype.BSFKL;
										
										for (int n = 0; n < 117; n++) {
											
											bconds[n] = random.nextInt(2);
											
											sconds[n] = random.nextInt(2);
											
											fconds[n] = random.nextInt(2);
											
											kconds[n] = random.nextInt(2);
											
											lconds[n] = random.nextInt(2);
											
										}
										
									}
									
								}
								
								rulestring = writerule();
								
							}
							
							int oldstatenum = statenum;
							
							rparse(rulestring);
							
							if (((ruletype != Ruletype.INT) && color) || (oldstatenum > statenum)) {
								
								for (int x = 0; x < 200; x++) {
									
									for (int y = 0; y < 200; y++) {
										
										if (world[y][x] > 1) world[y][x] = 1;
										
									}
									
								}
								
								color = false;
								
							}
							
							rulestring = "";
							
							mode = Mode.Running;
							
							setTitle("NaiViewer (" + writerule() + ")");
							
							repaint();
							
							break;
							
						case KeyEvent.VK_BACK_SPACE:
							
							String tempstring = "";
							
							for (int n = 0; n < (rulestring.length() - 1); n++) {
								
								tempstring += rulestring.charAt(n);
								
							}
							
							rulestring = tempstring;
							
							repaint();
							
							break;
							
					}
					
				}
				
			}
			
			public void keyTyped(KeyEvent ke) {
				
				if (mode == Mode.SettingRule) {
					
					if (ke.getKeyChar() != 8) rulestring += ke.getKeyChar(); // Prevents 'delete' characters from being appended to the rulestring
					
					if (tag) {
						
						rulestring = "";
						
						tag = false;
						
					}
					
					repaint();
					
				}
				
			}
			
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			public void mouseDragged(MouseEvent me) {
				
				switch (mode) {
					
					case Running:
						
						if (world[me.getY() / 4][me.getX() / 4] == 0) world[me.getY() / 4][me.getX() / 4] = 1;
						
						else world[me.getY() / 4][me.getX() / 4] = 0;
						
						cursor[0] = me.getY() / 4;
						
						cursor[1] = me.getX() / 4;
						
						if (selecting) {
						   	
							region[1][0] = cursor[0];
							
							region[1][1] = cursor[1];
							
						} else {
							
							if (carrying) {
								
								region[0][0] = cursor[0];
								
								region[0][1] = cursor[1];
								
								region[1][0] = cursor[0];
								
								region[1][1] = cursor[1];
								
							}
							
						}
						
						repaint();
						
						break;
						
				}
				
			}
			
		});
		
		addMouseListener(new MouseAdapter() {
			
			public void mouseClicked(MouseEvent me) {
				
				switch (mode) {
					
					case Running:
						
						if (world[me.getY() / 4][me.getX() / 4] == 0) world[me.getY() / 4][me.getX() / 4] = 1;
						
						else world[me.getY() / 4][me.getX() / 4] = 0;
						
						cursor[0] = me.getY() / 4;
						
						cursor[1] = me.getX() / 4;
						
						if (selecting) {
						   	
							region[1][0] = cursor[0];
							
							region[1][1] = cursor[1];
							
						} else {
							
							if (carrying) {
								
								region[0][0] = cursor[0];
								
								region[0][1] = cursor[1];
								
								region[1][0] = cursor[0];
								
								region[1][1] = cursor[1];
								
							}
							
						}
						
						repaint();
						
						break;
						
				}
				
			}
			
		});
		
	}
	
	void rparse(String rulestring) {
		
		int lastdigit = -1;
		
		char c;
		
		boolean negating = false;
		
		String letters = "cekainyqjrtwz";
		
		enum Set {
			
			birth, survival, generations, deficient, f, k, l;
			
		}
		
		Set set = Set.birth;
		
		this.naive = false;
		
		this.statenum = 2;
		
		this.ruletype = Ruletype.INT;
		
		for (int n = 0; n < rulestring.length(); n++) {
			
			c = rulestring.charAt(n);
			
			switch (c) {
				
				case 'B' -> set = Set.birth;
				
				case 'D' -> {
					
					this.statenum = 3;
					
					set = Set.deficient;
					
					ruletype = Ruletype.Deficient;
					
				}
				
				case 'F' -> {
					
					this.statenum = 3;
					
					set = Set.f;
					
					ruletype = Ruletype.BSFKL;
					
				}
				
				case 'G' -> {
					
					this.statenum = 0;
					
					set = Set.generations;
					
					ruletype = Ruletype.Generations;
					
				}
				
				case 'K' -> {
					
					this.statenum = 3;
					
					set = Set.k;
					
					ruletype = Ruletype.BSFKL;
					
				}
				
				case 'L' -> {
					
					this.statenum = 3;
					
					set = Set.l;
					
					ruletype = Ruletype.BSFKL;
					
				}
				
				case 'N' -> this.naive = true;
				
				case 'S' -> set = Set.survival;
					
				case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> {
					
					if (set != Set.generations) {
						
						lastdigit = (c - '0');
						
						for (int nn = 0; nn < 13; nn++) {
							
							switch (set) {
								
								case birth -> this.bconds[(lastdigit * 13) + nn] = 2;
								
								case survival -> this.sconds[(lastdigit * 13) + nn] = 2;
								
								case deficient -> this.dconds[(lastdigit * 13) + nn] = 2;
								
								case f -> this.fconds[(lastdigit * 13) + nn] = 2;
								
								case k -> this.kconds[(lastdigit * 13) + nn] = 2;
								
								case l -> this.lconds[(lastdigit * 13) + nn] = 2;
								
							}
							
						}
						
					} else {
						
						this.statenum *= 10;
						
						this.statenum += (c - '0');
						
					}
					
					negating = false;
					
				}
				
				case 'c', 'e', 'k', 'a', 'i', 'n', 'y', 'q', 'j', 'r', 't', 'w', 'z' -> {
					
					int i = letters.indexOf(c);
					
					if (negating) {
						
						switch (set) {
							
							case birth -> this.bconds[(lastdigit * 13) + i] = 0;
							
							case survival -> this.sconds[(lastdigit * 13) + i] = 0;
							
							case deficient -> this.dconds[(lastdigit * 13) + i] = 0;
							
							case f -> this.fconds[(lastdigit * 13) + i] = 0;
							
							case k -> this.kconds[(lastdigit * 13) + i] = 0;
							
							case l -> this.lconds[(lastdigit * 13) + i] = 0;
							
						}
						
					} else {
						
						for (int nn = 0; nn < 13; nn++) {
							
							switch (set) {
								
								case birth -> {
									
									if (this.bconds[(lastdigit * 13) + nn] == 2) this.bconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.bconds[(lastdigit * 13) + i] = 1;
									
								}
								
								case survival -> {
									
									if (this.sconds[(lastdigit * 13) + nn] == 2) this.sconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.sconds[(lastdigit * 13) + i] = 1;
									
								}
								
								case deficient -> {
									
									if (this.dconds[(lastdigit * 13) + nn] == 2) this.dconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.dconds[(lastdigit * 13) + i] = 1;
									
								}
								
								case f -> {
									
									if (this.fconds[(lastdigit * 13) + nn] == 2) this.fconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.fconds[(lastdigit * 13) + i] = 1;
									
								}
								
								case k -> {
									
									if (this.kconds[(lastdigit * 13) + nn] == 2) this.kconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.kconds[(lastdigit * 13) + i] = 1;
									
								}
								
								case l -> {
									
									if (this.lconds[(lastdigit * 13) + nn] == 2) this.lconds[(lastdigit * 13) + nn] = 0;
									
									if (nn == i) this.lconds[(lastdigit * 13) + i] = 1;
									
								}
								
							}
							
						}
						
					}
					
				}
				
				case '-' -> negating = true;
				
				default -> {
					
					continue;
					
				}
				
			}
			
		}
		
		if ((this.statenum == 2) && (this.ruletype == Ruletype.Generations)) this.ruletype = Ruletype.INT;
		
	}
	
	void advance() {
		
		// dict is a 256-element lookup table mapping neighborhood configurations to Hensel conditions
		
		int[] dict = {0, 13, 14, 29, 13, 26, 29, 43, 14, 29, 27, 42, 28, 44, 47, 55, 14, 28, 27, 47, 29, 44, 42, 55, 30, 48, 40, 61, 48, 56, 61, 69, 13, 26, 28, 44, 31, 39, 46, 57, 29, 43, 47, 55, 46, 57, 63, 68, 28, 45, 41, 54, 46, 58, 59, 73, 48, 62, 60, 70, 64, 74, 72, 81, 14, 28, 30, 48, 28, 45, 48, 62, 27, 47, 40, 61, 41, 54, 60, 70, 27, 41, 40, 60, 47, 54, 61, 70, 40, 60, 53, 65, 60, 71, 65, 78, 29, 44, 48, 56, 46, 58, 64, 74, 42, 55, 61, 69, 59, 73, 72, 81, 47, 54, 60, 71, 63, 67, 72, 80, 61, 70, 65, 78, 72, 80, 83, 91, 13, 31, 28, 46, 26, 39, 44, 57, 28, 46, 41, 59, 45, 58, 54, 73, 29, 46, 47, 63, 43, 57, 55, 68, 48, 64, 60, 72, 62, 74, 70, 81, 26, 39, 45, 58, 39, 52, 58, 66, 44, 57, 54, 73, 58, 66, 67, 79, 44, 58, 54, 67, 57, 66, 73, 79, 56, 74, 71, 80, 74, 82, 80, 92, 29, 46, 48, 64, 44, 58, 56, 74, 47, 63, 60, 72, 54, 67, 71, 80, 42, 59, 61, 72, 55, 73, 69, 81, 61, 72, 65, 83, 70, 80, 78, 91, 43, 57, 62, 74, 57, 66, 74, 82, 55, 68, 70, 81, 73, 79, 80, 92, 55, 73, 70, 80, 68, 79, 81, 92, 69, 81, 78, 91, 81, 92, 91, 104};
	    
	    int[][] neigh = {{1, 1}, {1, 0}, {1, -1}, {0, 1}, {0, -1}, {-1, 1}, {-1, 0}, {-1, -1}};
	    
	    int[][] next = new int[200][200];
	    
	    for (int x = 0; x < 200; x++) {
	        
	        for (int y = 0; y < 200; y++) {
	            
	            int xx, yy;
	            
	            int neighs = 0;
	            
	            int binary = 0;
	            
	            int sb = 0; // For deficient rules
	            
	            for (int n = 0; n < 8; n++) {
	                
	                xx = x + neigh[n][0];
	                
	                yy = y + neigh[n][1];
	                
	                if (!(this.naive)) {
	                		
	                		xx += 200;
	                		
	                		yy += 200;
	                		
	                		xx %= 200;
	                		
	                		yy %= 200;
	                		
	                	}
	                
	                binary *= 2;
	                
	                sb *= 2;
	                
	                if ((xx >= 200) || (yy >= 200) || (xx < 0) || (yy < 0)) continue;
	                
	                if (this.ruletype == Ruletype.Generations) {
	                		
	                		if (this.world[xx][yy] == 1) {
	                			
	                			binary++; // No neighs needed here
	                			
	                		}
	                		
	                	} else if (this.ruletype == Ruletype.Deficient) {
	                		
	                		if (this.world[xx][yy] != 0) binary++;
	                			
	                		if (this.world[xx][yy] == 2) sb++;
	                		
	                	} else if (this.ruletype == Ruletype.BSFKL) {
	                		
	                		if (this.world[xx][yy] == 1) binary++;
	                			
	                		if (this.world[xx][yy] == 2) sb++;
	                		
	                	} else {
	                		
	                		if (this.world[xx][yy] != 0) {
	                			
	                			binary++;
	                			
	                			neighs++;
	                			
	                		}
	                		
	                	}
	                	
	            }
	            
	            if (this.ruletype == Ruletype.INT) {	
	  	          
	    		        if (this.naive) {
	    		        		
	    		        		if (this.color) {
	    		        			
	    		    		    		if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) {
	    		    		    			
	    		    		    			this.world[x][y] = neighs + 1;
	    		    			    			
	    		    		    			continue; // Necessary to prevent the next condition from being evaluated if this one is true
	    		    		    			
	    		    		    		}
	    		    		    		
	    		    		    		if ((this.world[x][y] != 0) && (this.sconds[dict[binary]] != 0)) {
	    		    		    			
	    		    		    			this.world[x][y] = neighs + 1;
	    		    		    			
	    		    		    			continue;
	    		    		    			
	    		    		    		}
	    		    		    		
	    		    		    		if ((this.world[x][y] != 0) && (this.sconds[dict[binary]] == 0)) this.world[x][y] = 0;
	    		        			
	    		        		} else {
	    		        			
	    		    		    		if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) {
	    		    		    			
	    		    		    			this.world[x][y] = 1;
	    		    			    			
	    		    		    			continue; // Necessary to prevent the next condition from being evaluated if this one is true
	    		    		    			
	    		    		    		}
	    		    		    		
	    		    		    		if ((this.world[x][y] != 0) && (this.sconds[dict[binary]] == 0)) this.world[x][y] = 0;
	    		        			
	    		        		}
	    		        		
	    		        	} else {
	    		        		
	    		        		if (this.color) {
	    		        			
	    		        			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) next[x][y] = neighs + 1;
	    		        			
	    		    		    		if ((this.world[x][y] != 0) && (this.sconds[dict[binary]] != 0)) next[x][y] = neighs + 1;
	    		        			
	    		        		} else {
	    		        			
	    		        			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) next[x][y] = 1;
	    		        			
	    		    		    		if ((this.world[x][y] != 0) && (this.sconds[dict[binary]] != 0)) next[x][y] = 1;
	    		        			
	    		        		}
	    		        		
	    		        	}
	            	
	            	}
	            	
	            	if (this.ruletype == Ruletype.Generations) {
	            		
	            		if (naive) {
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) {
	    		    		    		
	    		    		    		this.world[x][y] = 1;
	    		    			    		
	    		    		    		continue; // Necessary to prevent the next condition from being evaluated if this one is true
	    		    		    		
	    		    		    	}
	    		    		    	
	    		    		    	if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] == 0)) {
	    		    		    		
	    		    		    		if (this.world[x][y] == (this.statenum - 1)) this.world[x][y] = 0;
	    		    		    		
	    		    		    		else this.world[x][y]++;
	    		    		    		
	    		    		    		continue;
	    		    		    		
	    		    		    	}
	    		    		    	
	    		    		    	if (this.world[x][y] > 1) {
	    		    		    		
	    		    		    		if (this.world[x][y] == (this.statenum - 1)) this.world[x][y] = 0;
	    		    		    		
	    		    		    		else this.world[x][y]++;
	    		    		    		
	    		    		    	}
	    		    		    	
	    		    		} else {
	    		    			
	    		    			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0)) next[x][y] = 1;
	    		    			
	    		    			if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] != 0)) next[x][y] = 1;
	    		    			
	    		    			if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] == 0)) {
	    		    		    		
	    		    		    		if (this.world[x][y] == (this.statenum - 1)) next[x][y] = 0;
	    		    		    		
	    		    		    		else next[x][y] = world[x][y] + 1;
	    		    		    		
	    		    		    	}
	    		    		    	
	    		    			if (this.world[x][y] > 1) {
	    		    				
	    		    		    		if (this.world[x][y] == (this.statenum - 1)) next[x][y] = 0;
	    		    		    		
	    		    		    		else next[x][y] = world[x][y] + 1;
	    		    		    		
	    		    		    	}
	    		    		    	
	    		    		}
	            		
	            	}
	            	
	            	if (this.ruletype == Ruletype.Deficient) {
	            		
	            		if (naive) {
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.dconds[dict[binary]] == 0)) {
	            				
	            				this.world[x][y] = 1;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.dconds[dict[binary]] != 0) && (sb == 0)) {
	            				
	            				this.world[x][y] = 2;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 2) && (this.sconds[dict[binary]] != 0)) {
	            				
	            				this.world[x][y] = 1;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 2) && (this.sconds[dict[binary]] == 0)) {
	            				
	            				this.world[x][y] = 0;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] == 0)) this.world[x][y] = 0;
	            			
	            		} else {
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.dconds[dict[binary]] == 0)) next[x][y] = 1;
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.dconds[dict[binary]] != 0) && (sb == 0)) next[x][y] = 2;
	            			
	            			if ((this.world[x][y] == 2) && (this.sconds[dict[binary]] != 0)) next[x][y] = 1;
	            			
	            			if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] != 0)) next[x][y] = 1;
	            			
	            		}
	            		
	            	}
	            	
	            	if (this.ruletype == Ruletype.BSFKL) {
	            		
	            		if (naive) {
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.fconds[dict[sb]] != 0)) {
	            				
	            				this.world[x][y] = 1;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 1) && (this.kconds[dict[sb]] != 0)) {
	            				
	            				this.world[x][y] = 0;
	            				
	            				continue;
	            				
	            			} else if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] != 0)) {
	            				
	            				continue;
	            				
	            			} else if (this.world[x][y] == 1) {
	            				
	            				this.world[x][y] = 2;
	            				
	            				continue;
	            				
	            			}
	            			
	            			if ((this.world[x][y] == 2) && (this.lconds[dict[binary]] != 0)) this.world[x][y] = 0;
	            			
	            		} else {
	            			
	            			if ((this.world[x][y] == 0) && (this.bconds[dict[binary]] != 0) && (this.fconds[dict[sb]] != 0)) next[x][y] = 1;
	            			
	            			if ((this.world[x][y] == 1) && (this.kconds[dict[sb]] != 0)) next[x][y] = 0;
	            			
	            			else if ((this.world[x][y] == 1) && (this.sconds[dict[binary]] != 0)) next[x][y] = 1;
	            			
	            			else if (this.world[x][y] == 1) next[x][y] = 2;
	            			
	            			if ((this.world[x][y] == 2) && (this.lconds[dict[binary]] != 0)) next[x][y] = 0;
	            			
	            			else if (this.world[x][y] == 2) next[x][y] = 2;
	            			
	            		}
	            		
	            	}
	            	
	        }
	        
	    }
	    
	    if (!(this.naive)) this.world = next;
	    
	}
	
	Color avg(Color c1, Color c2) {
		
		int r = (c1.getRed() + c2.getRed()) / 2;
		
		int g = (c1.getGreen() + c2.getGreen()) / 2;
		
		int b = (c1.getBlue() + c2.getBlue()) / 2;
		
		return new Color(r, g, b);
		
	}
	
	Color gencolor(int n) {
		
		int r, g, b;
		
		int[] finalcolor = new int[3];
		
		if (n > 0) {
			
			r = (this.statenum - 1 - n) * (255 / (this.statenum - 2));
			
			g = r;
			
			b = 255;
			
			return new Color(r, g, b);
			
		}
		
		return Color.black;
		
	}
		
	String sort(String string) { // Borrowed from GlidINT
		
		String sorted = "";
		
		for (char n = 'a'; n <= 'z'; n++) {
			
			for (int c = 0; c < string.length(); c++) {
				
				if (string.charAt(c) == n) sorted += n;
				
			}
			
		}
		
		return sorted;
		
	}
	
	String writerule() { // Borrowed from GlidINT, adapted
		
		String rulestring = "";
		
		String letters = "cekainyqjrtwz";
		
		String set;
		
		String sorted;
		
		int c;
		
		int[] limits = {1, 2, 6, 10, 13, 10, 6, 2, 1};
		
		if (this.naive) rulestring += "N";
		
		rulestring += "B";
		
		for (int n = 0; n <= 8; n++) {
			
			set = "";
			
			sorted = "";
			
			c = 0;
			
			for (int nn = 0; nn < limits[n]; nn++) {
				
				if (this.bconds[(13 * n) + nn] != 0) {
					
					c++;
					
				}
				
			}
			
			if (c == 0) {
				
				continue;
				
			} else if (c == limits[n]) {
				
				rulestring += (char) (n + '0');
				
			} else if (c <= Math.ceilDiv(limits[n], 2)) {
				
				rulestring += (char) (n + '0');
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.bconds[(13 * n) + nn] != 0) {
						
						set += letters.charAt(nn);
						
					}
					
				}
				
				sorted = sort(set);
				
				for (int nnn = 0; nnn < sorted.length(); nnn++) {
					
					rulestring += sorted.charAt(nnn);
					
				}
				
			} else if (c < limits[n]) {
				
				rulestring += (char) (n + '0');
				
				rulestring += "-";
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.bconds[(13 * n) + nn] == 0) {
						
						set += letters.charAt(nn);
						
					}
					
				}
				
				sorted = sort(set);
				
				for (int nnn = 0; nnn < sorted.length(); nnn++) {
					
					rulestring += sorted.charAt(nnn);
					
				}
				
			}
			
		}
		
		rulestring += "/S";
		
		for (int n = 0; n <= 8; n++) {
			
			set = "";
			
			sorted = "";
			
			c = 0;
			
			for (int nn = 0; nn < limits[n]; nn++) {
				
				if (this.sconds[(13 * n) + nn] != 0) {
					
					c++;
					
				}
				
			}
			
			// System.out.println(n + " " + c);
			
			if (c == 0) {
				
				continue;
				
			} else if (c == limits[n]) {
				
				rulestring += (char) (n + '0');
				
			} else if (c <= Math.ceilDiv(limits[n], 2)) {
				
				rulestring += (char) (n + '0');
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.sconds[(13 * n) + nn] != 0) {
						
						set += letters.charAt(nn);
						
					}
					
				}
				
				sorted = sort(set);
				
				// System.out.println(sorted);
				
				for (int nnn = 0; nnn < sorted.length(); nnn++) {
					
					rulestring += sorted.charAt(nnn);
					
				}
				
			} else if (c < limits[n]) {
				
				rulestring += (char) (n + '0');
				
				rulestring += "-";
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.sconds[(13 * n) + nn] == 0) {
						
						set += letters.charAt(nn);
						
					}
					
				}
				
				sorted = sort(set);
				
				for (int nnn = 0; nnn < set.length(); nnn++) {
					
					rulestring += sorted.charAt(nnn);
					
				}
				
			}
			
		}
		
		if ((this.ruletype == Ruletype.Generations) && (this.statenum > 2)) {
			
			rulestring += "/G";
			
			rulestring += this.statenum;
			
		}
		
		if (this.ruletype == Ruletype.Deficient) {
			
			rulestring += "/D";
			
			for (int n = 0; n <= 8; n++) {
				
				set = "";
				
				sorted = "";
				
				c = 0;
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.dconds[(13 * n) + nn] != 0) {
						
						c++;
						
					}
					
				}
				
				// System.out.println(n + " " + c);
				
				if (c == 0) {
					
					continue;
					
				} else if (c == limits[n]) {
					
					rulestring += (char) (n + '0');
					
				} else if (c <= Math.ceilDiv(limits[n], 2)) {
					
					rulestring += (char) (n + '0');
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.dconds[(13 * n) + nn] != 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					// System.out.println(sorted);
					
					for (int nnn = 0; nnn < sorted.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				} else if (c < limits[n]) {
					
					rulestring += (char) (n + '0');
					
					rulestring += "-";
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.dconds[(13 * n) + nn] == 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					for (int nnn = 0; nnn < set.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				}
				
			}
			
		}
		
		if (this.ruletype == Ruletype.BSFKL) {
			
			rulestring += "/F";
			
			for (int n = 0; n <= 8; n++) {
				
				set = "";
				
				sorted = "";
				
				c = 0;
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.fconds[(13 * n) + nn] != 0) {
						
						c++;
						
					}
					
				}
				
				// System.out.println(n + " " + c);
				
				if (c == 0) {
					
					continue;
					
				} else if (c == limits[n]) {
					
					rulestring += (char) (n + '0');
					
				} else if (c <= Math.ceilDiv(limits[n], 2)) {
					
					rulestring += (char) (n + '0');
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.fconds[(13 * n) + nn] != 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					// System.out.println(sorted);
					
					for (int nnn = 0; nnn < sorted.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				} else if (c < limits[n]) {
					
					rulestring += (char) (n + '0');
					
					rulestring += "-";
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.fconds[(13 * n) + nn] == 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					for (int nnn = 0; nnn < set.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				}
				
			}
			
			rulestring += "/K";
			
			for (int n = 0; n <= 8; n++) {
				
				set = "";
				
				sorted = "";
				
				c = 0;
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.kconds[(13 * n) + nn] != 0) {
						
						c++;
						
					}
					
				}
				
				// System.out.println(n + " " + c);
				
				if (c == 0) {
					
					continue;
					
				} else if (c == limits[n]) {
					
					rulestring += (char) (n + '0');
					
				} else if (c <= Math.ceilDiv(limits[n], 2)) {
					
					rulestring += (char) (n + '0');
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.kconds[(13 * n) + nn] != 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					// System.out.println(sorted);
					
					for (int nnn = 0; nnn < sorted.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				} else if (c < limits[n]) {
					
					rulestring += (char) (n + '0');
					
					rulestring += "-";
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.kconds[(13 * n) + nn] == 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					for (int nnn = 0; nnn < set.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				}
				
			}
			
			rulestring += "/L";
			
			for (int n = 0; n <= 8; n++) {
				
				set = "";
				
				sorted = "";
				
				c = 0;
				
				for (int nn = 0; nn < limits[n]; nn++) {
					
					if (this.lconds[(13 * n) + nn] != 0) {
						
						c++;
						
					}
					
				}
				
				// System.out.println(n + " " + c);
				
				if (c == 0) {
					
					continue;
					
				} else if (c == limits[n]) {
					
					rulestring += (char) (n + '0');
					
				} else if (c <= Math.ceilDiv(limits[n], 2)) {
					
					rulestring += (char) (n + '0');
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.lconds[(13 * n) + nn] != 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					// System.out.println(sorted);
					
					for (int nnn = 0; nnn < sorted.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				} else if (c < limits[n]) {
					
					rulestring += (char) (n + '0');
					
					rulestring += "-";
					
					for (int nn = 0; nn < limits[n]; nn++) {
						
						if (this.lconds[(13 * n) + nn] == 0) {
							
							set += letters.charAt(nn);
							
						}
						
					}
					
					sorted = sort(set);
					
					for (int nnn = 0; nnn < set.length(); nnn++) {
						
						rulestring += sorted.charAt(nnn);
						
					}
					
				}
				
			}
			
		}
		
		return rulestring;
		
	}
	
	@Override
	
	public void update(Graphics g) {
		
		paint(g);
		
	}
	
	@Override
	
	public void paint(Graphics g) {
		
		switch (mode) {
			
			case Running:
				
				int minx = (this.region[0][0] < this.region[1][0]) ? this.region[0][0] : this.region[1][0];
				
				int maxx = (this.region[0][0] > this.region[1][0]) ? this.region[0][0] : this.region[1][0];
				
				int miny = (this.region[0][1] < this.region[1][1]) ? this.region[0][1] : this.region[1][1];
				
				int maxy = (this.region[0][1] > this.region[1][1]) ? this.region[0][1] : this.region[1][1];
				
				if (this.color) {
					
					Color[] colors = {Color.black, Color.white, Color.gray, Color.red, Color.green, Color.blue, Color.orange, new Color(128, 000, 255), Color.yellow, Color.magenta};
					
			    		for (int x = 0; x < 200; x++) {
						
						for (int y = 0; y < 200; y++) {
							
							if ((minx <= x) && (x < maxx) && (miny <= y) && (y < maxy)) {
								
								Color current = colors[this.world[x][y]];
								
								Color overlay = avg(new Color(0, 255, 0), current);
								
								g.setColor(overlay);
								
							} else {
								
								g.setColor(colors[this.world[x][y]]);
								
							}
							
							g.fillRect(4 * y, 4 * x, 4, 4);
							
						}
						
					}
					
					g.setColor(avg(new Color(255, 0, 0), colors[this.world[this.cursor[0]][this.cursor[1]]]));
					
					g.fillRect(4 * this.cursor[1], 4 * this.cursor[0], 4, 4);
					
				} else {
					
					if (this.ruletype == Ruletype.INT) {
						
						Color[] colors = {Color.black, Color.white};
						
				    		for (int x = 0; x < 200; x++) {
							
							for (int y = 0; y < 200; y++) {
								
								if ((minx <= x) && (x < maxx) && (miny <= y) && (y < maxy)) {
									
									Color current = colors[this.world[x][y]];
									
									Color overlay = avg(new Color(0, 255, 0), current);
									
									g.setColor(overlay);
									
								} else {
									
									g.setColor(colors[this.world[x][y]]);
									
								}
								
								g.fillRect(4 * y, 4 * x, 4, 4);
								
							}
							
						}
						
						g.setColor(avg(new Color(255, 0, 0), colors[this.world[this.cursor[0]][this.cursor[1]]]));
						
						g.fillRect(4 * this.cursor[1], 4 * this.cursor[0], 4, 4);
						
					}
					
				}
				
				if (this.ruletype == Ruletype.Generations) {
					
				    	for (int x = 0; x < 200; x++) {
						
						for (int y = 0; y < 200; y++) {
							
							if ((minx <= x) && (x < maxx) && (miny <= y) && (y < maxy)) {
								
								Color current = gencolor(this.world[x][y]);
								
								Color overlay = avg(new Color(0, 255, 0), current);
								
								g.setColor(overlay);
								
							} else {
								
								g.setColor(gencolor(this.world[x][y]));
								
							}
							
							g.fillRect(4 * y, 4 * x, 4, 4);
							
						}
						
					}
					
					g.setColor(avg(new Color(255, 0, 0), gencolor(this.world[this.cursor[0]][this.cursor[1]])));
					
					g.fillRect(4 * this.cursor[1], 4 * this.cursor[0], 4, 4);
					
				}
				
				if (this.ruletype == Ruletype.Deficient) {
					
					Color[] colors = {Color.black, Color.white, Color.yellow};
					
				    	for (int x = 0; x < 200; x++) {
						
						for (int y = 0; y < 200; y++) {
							
							if ((minx <= x) && (x < maxx) && (miny <= y) && (y < maxy)) {
								
								Color current = colors[this.world[x][y]];
								
								Color overlay = avg(new Color(0, 255, 0), current);
								
								g.setColor(overlay);
								
							} else {
								
								g.setColor(colors[this.world[x][y]]);
								
							}
							
							g.fillRect(4 * y, 4 * x, 4, 4);
							
						}
						
					}
					
					g.setColor(avg(new Color(255, 0, 0), colors[this.world[this.cursor[0]][this.cursor[1]]]));
					
					g.fillRect(4 * this.cursor[1], 4 * this.cursor[0], 4, 4);
					
				}
				
				if (this.ruletype == Ruletype.BSFKL) {
					
					Color[] colors = {Color.black, new Color(0, 192, 255), Color.red};
					
				    	for (int x = 0; x < 200; x++) {
						
						for (int y = 0; y < 200; y++) {
							
							if ((minx <= x) && (x < maxx) && (miny <= y) && (y < maxy)) {
								
								Color current = colors[this.world[x][y]];
								
								Color overlay = avg(new Color(0, 255, 0), current);
								
								g.setColor(overlay);
								
							} else {
								
								g.setColor(colors[this.world[x][y]]);
								
							}
							
							g.fillRect(4 * y, 4 * x, 4, 4);
							
						}
						
					}
					
					g.setColor(avg(new Color(255, 0, 0), colors[this.world[this.cursor[0]][this.cursor[1]]]));
					
					g.fillRect(4 * this.cursor[1], 4 * this.cursor[0], 4, 4);
					
				}
				
				break;
				
			case SettingRule:
				
				g.setColor(Color.black);
				
				g.fillRect(0, 0, 800, 800);
				
				g.setColor(Color.white);
				
				g.drawString(this.rulestring, 400, 400);
				
				break;
				
		}
		
	}
		
}

class NaiViewer {
	
	public static void main(String[] args) {
		
		Funcs funcs = new Funcs();
		
		funcs.setBackground(Color.black);
		
		funcs.setForeground(Color.white);
		
		funcs.setSize(new Dimension(800, 800));
		
		funcs.setTitle("NaiViewer (B3/S23)");
		
		funcs.setVisible(true);
		
		funcs.rparse("B3/S23");
		
	}
	
}
