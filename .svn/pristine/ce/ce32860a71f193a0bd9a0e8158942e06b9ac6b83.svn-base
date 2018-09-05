package com.lostagain.Jam.iJammerSpecific;

/**
 * Utility class not used in the engine, but might be by the iJammer editor
 * Its for producing the needed JAM script code for a image split into a load of tiles. 
 * 
 * @author darkflame
 *
 */
public class JamCodeForImageSplit {

	public enum SplitMode {
		HorizontalLines,
		VerticalLines
	}

	/**Produces jam code like;
	 * -Item:
 Name = BaseFilename_1.png
 Frames = 1
 Located = 0,0

 -Item:
 Name = BaseFilename_2.png
 Frames = 1
 Located = 0,400
..etc
	 * 
	 * @param baseFileName
	 * @param numHorPieces
	 * @param numVertPieces
	 * @param totalWidth
	 * @param totalHeight
	 * @param mode - split to horizontal lines first, or vertical
	 * @return
	 */
	public static String getCodeForSplit(String baseFileName, 
			int numHorPieces, int numVertPieces, 
			int totalWidth,   int totalHeight, SplitMode mode ) {



		String code = "";
		int segmentWidth  =   totalWidth/numHorPieces;
		int segmentHeight = totalHeight/numVertPieces;
		int piecenumber = 1; //Krita starts from 1, but we might want options to start from 0
		
		if (mode == SplitMode.VerticalLines){

			for (int w = 0; w < numHorPieces; w++) {

				int currentx = w * segmentWidth;

				for (int h = 0; h < numVertPieces; h++) {
						int currenty = h * segmentHeight;		
						
					code=code+ getCodeForItem(baseFileName,piecenumber,currentx,currenty);					
					piecenumber=piecenumber+1;			

				}					

			}


		} else {
			//verticallines

			for (int h = 0; h < numVertPieces; h++) {

				int currenty = h * segmentHeight;	

				for (int w = 0; w < numHorPieces; w++) {

					int currentx = w * segmentWidth;


					code=code+ getCodeForItem(baseFileName,piecenumber,currentx,currenty);
					
					piecenumber=piecenumber+1;			

				}					

			}


		}

		return code;		


	}

	public static String getCodeForItem(String baseFileName, int pieceNumber, int x, int y){

		String currentfilename = baseFileName+pieceNumber+".png";

		String code=
				"\r\n" + 
						" -Item:\r\n" + 
						" Name = "+currentfilename+"\r\n" + 
						" Frames = 1\r\n" + 
						" Located = "+x+","+y+"\r\n";

		return code;


	}

}
