package compiler488.utilities;

import java.util.ArrayList;
import java.util.Collections;


public class Temp {
	public static void getMineGrid(int[][] H, int[][] G, int d) {
		int n = H.length;
		int m = H[0].length;
		
		int[][][] A = new int[n][m][d+1];
		
		// The base case is when k=0
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				if (H[i][j] == 0) {
					A[i][j][0] = G[i][j];
				} else {
					A[i][j][0] = Integer.MIN_VALUE;
				}
			}
		}
				
		//Now do the dynamic programming algorithm
		for (int k = 1; k <= d; k++) {
			for (int i = 1; i < n; i++) {
				for (int j = 0; j < m; j++) {
					ArrayList<Integer> possibleValues = new ArrayList<Integer>();
					
					int h = H[i][j];
					
					if (k - h >= 0) {
						if (j > 0)
							possibleValues.add(A[i-1][j-1][k-h]);
						possibleValues.add(A[i-1][j][k-h]);
						if (j < n - 1)
						possibleValues.add(A[i-1][j+1][k-h]);
					}
					
					System.out.println("Figuring out a value for [" + i + "][" + j + "][" + k + "]");
					System.out.println("  Choices are " + possibleValues);
					
					A[i][j][k] = possibleValues.isEmpty() ? Integer.MIN_VALUE : Collections.max(possibleValues) + G[i][j];
					
					System.out.print("  Choose: " + A[i][j][k]);
					System.out.println();
				}
			}
		}
		
		//Print the resulting grid.
		for (int i = 1; i < n; i++) {
			for (int j = 0; j < m; j++) {
				System.out.print(A[i][j][d] + "\t");
			}
			System.out.println();
		}
	}
	
	/*public static void main(String[] args) {
		int[][] H = {{0,0,0,0,0},
					 {2,2,0,2,1},
					 {2,0,1,2,1},
					 {1,1,2,2,3}};
		
		int[][] G = {{0,0,0,0,0},
					 {2,7,0,3,8},
					 {4,0,2,0,1},
					 {4,1,6,1,8}};
		
		getMineGrid(H, G, 4);
	}*/
}
