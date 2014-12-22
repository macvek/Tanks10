/*
This file is part of Tanks10 Project (https://github.com/macvek/Tanks10).

Tanks10 Project is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Tanks10 Project is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Tanks10 Project.  If not, see <http://www.gnu.org/licenses/>.
*/
package tanks10.world;
/**
 * Klasa wykorzystywana przy kolizjach. Wszystkie siatki są stworzone z trójkątów
 * @author Macvek
 *
 */
public class Triangle {
	static final public int X=0,Y=1;
	static public double[][] collideLine; 
	private double[][] mesh = new double[3][2];
	public double[][] get() {
		double[][] ret = new double[mesh.length][];
		for (int i=0;i<mesh.length;i++) {
			ret[i] = mesh[i].clone();
		}
		return ret;
	
	}	// to jest ważne, że clone()
	public void set(double[][] nMesh) { mesh = nMesh; }
	
	public Triangle() {}
	public Triangle(double[][] nMesh) { set(nMesh);}
	
	public static double[][] rotate(double[][] m, double angle) {
		double x,y;
		for (int i=0;i<3;i++) {
			x = m[i][X]*Math.cos(angle) - m[i][Y]*Math.sin(angle);
			y = m[i][X]*Math.sin(angle) + m[i][Y]*Math.cos(angle);
			
			m[i][X] = x;
			m[i][Y] = y;
		}
		return m;
	}
	
	public static double[][] translate(double[][] m, double[] offset) {
		for (int i=0;i<3;i++) {
			m[i][X]+=offset[X];
			m[i][Y]+=offset[Y];
		}
		
		return m;
	}
	
	// wylicza współczynnik kierunkowy
	private static double argument(double[][] a) {
		return (a[1][Y] - a[0][Y])/(a[1][X] - a[0][X]);
	}
	
	// wylicza przesunięcie b
	private static double offset(double[][] a, double arg) {
		return a[0][Y] - arg * a[0][X];
	}
	
	private static boolean between(double[][] a, double value, int index) {
		double minA = Math.min(a[0][index], a[1][index]);
		double maxA = Math.max(a[0][index], a[1][index]);
		
		if (value < minA || value > maxA)
			return false;
		
		return true;
	}
	
	// skrajny przypadek
	private static boolean wallTest(double[][] a, double[][] wall) {

		double argA = argument(a);
		double offsetA = offset(a, argA);
		
		double jointY = argA*wall[0][X] + offsetA;
		return ( (between(wall,jointY,Y) && between(a,wall[0][X],X)) );
	}
	
	// ogólny przypadek
	private static boolean segmentTest(double[][] a, double[][] b) {
		double argA = argument(a);
		double offsetA = offset(a, argA);
		
		double argB = argument(b);
		double offsetB = offset(b, argB);
		
		// X i Y dla którego przecinają się odcinki
		double jointX = (offsetA - offsetB)/(argB - argA);
		double jointY = argA*jointX + offsetA;
		
		return ( between(a, jointX, X) && between(b, jointX, X) 
				&& between(a, jointY, Y) && between(b, jointY, Y));
	}
	
	/* Test krawędzi
	 * Zaznaczam, że jest to bardzo poglądowy test i nie zastanawiałem się bardzo
	 * nad jego szybkością. Sprawdza brutalnie każdy z każdym.
	 * Modele niewiele trójkątów więc nie powinno to być problemem.
	 * Podobnie jak sprawdzanie krawędzi, które jest też nie koniecznie doskonałe
	 */
	
	public static boolean edgeTest(double[][] a, double[][] b) {
		//X rośnie w prawo, Y rośnie w dół

		double tmpPoint[];
		
		// punkty mniejsze są 0, większe na 1 (na osi X)
		if (a[0][X] > a[1][X]) { tmpPoint = a[0]; a[0] = a[1]; a[1] = tmpPoint; }
		if (b[0][X] > b[1][X]) { tmpPoint = b[0]; b[0] = b[1]; b[1] = tmpPoint; }
		
		// test czy nie są w poziomie
		if (a[0][Y] == a[1][Y] && b[0][Y] == b[1][Y]) {
			if ((a[0][Y] == b[0][Y]) && ( between(b,a[0][X],X) || between(b,a[1][X],X) )) {
				return true;
			}
				
			return false;
		}
		
		if (a[0][X] == a[1][X]) {
			if (b[0][X] == b[1][X]) {
	// oba są w pionie, więc sprawdzamy czy któryś z wierzchołków A jest między A i B w osi Y
				if ((a[0][X] == b[0][X]) && (between(b,a[0][Y],Y) || between(b,a[1][Y],Y))) {
					return true;
				}

				return false;
			}
			
			return wallTest(b,a);
		}
		else 
			// inne zderzenie ze ścianą
			if (b[0][X] == b[1][X]) 
				return wallTest(a,b);
		
		// dla obu odcinków można wyliczyć równanie prostej
		
		return segmentTest(a,b);
	}
	
	// a i b to są punkty trójkątów
	public static boolean collision(double[][] a, double[][] b) {
		// test kolizji polega na sprawdzeniu czy krawędzie się przecinają
		
		double[][] edgeA;
		double[][] edgeB;
		
		// wyznaczenie krawędzi
		for (int i=0;i<3;i++) {
			edgeA = new double[2][2];
			edgeA[0][X] = a[i][X]; edgeA[1][X] = a[(i+1) % 3][X];
			edgeA[0][Y] = a[i][Y]; edgeA[1][Y] = a[(i+1) % 3][Y];
			for (int j=0;j<3;j++) {
				edgeB = new double[2][2];
				edgeB[0][X] = b[j][X]; edgeB[1][X] = b[(j+1) % 3][X];
				edgeB[0][Y] = b[j][Y]; edgeB[1][Y] = b[(j+1) % 3][Y];
				
				if (edgeTest(edgeA, edgeB)) {	// test kolizji 
					collideLine = edgeB;
					return true;
					// drugi trójkąt to najczęście to o kogo się obił, więc zapamiętaj dla obliczenia odbicia
				}
				
			}
		}
		
		return false;
	}
	
	public static boolean meshTest(Triangle[] a, double[] translateA, double rotateA,
									Triangle[] b, double[] translateB, double rotateB) {
		
		double[][] triangleA,triangleB;
		for (int iA=0;iA<a.length;iA++) {
			triangleA = Triangle.translate(	// przesuń obrócony trójkąt
							Triangle.rotate(a[iA].get(),rotateA),	// obróć trójkąt 
							translateA);
			for (int iB=0;iB<b.length;iB++) {
				triangleB = Triangle.translate(	// przesuń obrócony trójkąt
						Triangle.rotate(b[iB].get(),rotateB),	// obróć trójkąt 
						translateB);
				
				// mamy przygotowane oba trójkąty, więc test
				if (collision(triangleA, triangleB))
					return true;
			}
		}
		
		return false;
	}
}
