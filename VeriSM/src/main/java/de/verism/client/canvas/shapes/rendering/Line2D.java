package de.verism.client.canvas.shapes.rendering;

import de.verism.client.canvas.shapes.Rectangle;


/**
 * This is a ported awt class providing line calculations.
 */
public abstract class Line2D {

    /**
     * Returns the distance from a point to a line segment.
     * The distance measured is the distance between the specified
     * point and the closest point between the specified end points.
     * 
     * Based on {@link java.awt.geom.Line2D}, slightly optimized for this application.
     */
    public static double ptSegDist(Point s, Point e, double px, double py) {
    	double sx = s.getX();
    	double sy = s.getY();
    	double ex = e.getX();
    	double ey = e.getY();
    	
        // Adjust vectors relative to x1,y1
        // x2,y2 becomes relative vector from x1,y1 to end of segment
        ex -= sx;
        ey -= sy;
        
        // px,py becomes relative vector from x1,y1 to test point
        px -= sx;
        py -= sy;
        
        // Punktprodukt
        double dotprod = px * ex + py * ey;
        double projlenSq;
        
        if (dotprod <= 0.0) {
            // point is outside of the line start and end points
            projlenSq = 0.0;
        } else {
            px = ex - px;
            py = ey - py;
            dotprod = px * ex + py * ey;
            if (dotprod <= 0.0) {
	            // outside the range of the line segment
	            projlenSq = 0.0;
            } else {
            	// inside the line area.
            	// dotprod is the length of the P vector projected on
            	// the line times its length.
	            projlenSq = dotprod * dotprod / (ex * ex + ey * ey);
            }
        }
        
        // the shortest distance
        double lenSq = px * px + py * py - projlenSq;
        
        if (lenSq < 0) {
            lenSq = 0;
        }
        return Math.sqrt(lenSq);
    }
    
    
    /**
     * Computes the intersection point of the line with the rectangle it connects.
	 * The clipping algorithm used is: Liang-Barsky
     * @param s the start point of the line
     * @param e the end point of the line
     * @param rectangle the rectangle to be crossed
     * @param takeFirst if first or 2nd intersection point should be taken
     * @return the intersection point
     */
	public static Point getIntersection(Point s, Point e, Rectangle rectangle, boolean takeFirst) {
		//store variables to work with
		double sx = s.getX();
		double sy = s.getY();
		double ex = e.getX();
		double ey = e.getY();

		//the clipping window lines defining the outer rectangle area
		double edgeLeft =  rectangle.getPosStart().getX();
		double edgeRight = rectangle.getPosEnd().getX();
		double edgeBottom = rectangle.getPosEnd().getY();
		double edgeTop = rectangle.getPosStart().getY();
		
		//distance of the linear equation points
		double dx = ex - sx;
		double dy = ey - sy;
		
		//points defining start and end point for the linear equation (x = x0 + t * dx, y = y0 + t * dy)
		double tmin = 0.0;
		double tmax = 1.0;
		
		//the intersection point
		double t;
		
		//left, right, bottom, top edge
		double p[] = new double[] {-dx, dx, -dy, dy};
		double q[] = new double[] {sx - edgeLeft, edgeRight - sx, sy - edgeBottom, edgeTop - sy};

		// Traverse through left, right, bottom, top edges.
		for (int edge = 0; edge < 4; edge++) {
			//the current intersection point to be tested
			t = q[edge] / p[edge];
			
			//line parallel to edge and outside the rectangle
			if(p[edge] == 0 && q[edge] < 0) {
				continue; //each continue will break the current edge calculation and continue with the next edge in the array
			}

			//line from outside to inside the clipping window
			if (p[edge] < 0) {
				if (t > tmax) {
					continue;
				}
				else if (t > tmin) {
					tmin = t;
				}
			//line from inside to outside clipping window
			} else if (p[edge] > 0) {
				if (t < tmin) {
					continue;
				}
				else if (t < tmax) {
					tmax = t;
				}
			}
		}
		
		double temp = takeFirst ? tmin : tmax;
		
		//swap arguments if t is still = 0.0 or 1.0, which means the actual t does not define the intersection point
		if (temp == 0.0 | temp == 1.0) {
			temp = takeFirst ? tmax : tmin;
		}
		
		//create the intersection point
		double x0clip = Math.round(sx + temp * dx);
		double y0clip = Math.round(sy + temp * dy);
		return new Point(x0clip, y0clip);
	}
	
}
