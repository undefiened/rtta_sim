/* File InvertedRay2D.java 
 *
 * Project : Java Geometry Library
 *
 * ===========================================
 * 
 * This library is free software; you can redistribute it and/or modify it 
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or (at
 * your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY, without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library. if not, write to :
 * The Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

// package

package math.geom2d.line;

import java.awt.geom.GeneralPath;

import math.geom2d.*;
import math.utils.EqualUtils;

// Imports

/**
 * Inverted ray is defined from an origin and a direction vector. It is composed
 * of all points satisfying the parametric equation:
 * <p>
 * <code>x(t) = x0+t*dx<code><br>
 * <code>y(t) = y0+t*dy<code></p> 
 * with <code>t<code> comprised between -INFINITY and 0.
 * This is complementary class to Ray2D.
 */
public class InvertedRay2D extends AbstractLine2D implements Cloneable {

    // ===================================================================
    // Static factory

    /**
     * Static factory for creating a new inverted ray with given direction
     * to target.
	 * @deprecated since 0.11.1
	 */
	@Deprecated
    public static InvertedRay2D create(Point2D target, Vector2D direction) {
    	return new InvertedRay2D(target, direction);
    }
    
    
    // ===================================================================
    // constructors

    /**
     * Empty constructor for Ray2D. Default is ray starting at origin, and
     * having a slope of 1*dx and 0*dy.
     */
    public InvertedRay2D() {
        this(0, 0, 1, 0);
    }

    /**
     * Creates a new Ray2D, originating from
     * <code>point1<\code>, and going in the
     * direction of <code>point2<\code>.
     */
    public InvertedRay2D(Point2D point1, Point2D point2) {
        this(point1.x(), point1.y(), 
        		point2.x()-point1.x(), 
        		point2.y()-point1.y());
    }

    /**
     * Creates a new Ray2D, originating from point <code>point<\code>, and going 
     * in the direction defined by vector <code>(dx,dy)<\code>.
     */
    public InvertedRay2D(Point2D point, double dx, double dy) {
        this(point.x(), point.y(), dx, dy);
    }

    /**
     * Creates a new Ray2D, originating from point <code>point<\code>, and going 
     * in the direction specified by <code>vector<\code>.
     */
    public InvertedRay2D(Point2D point, Vector2D vector) {
        this(point.x(), point.y(), vector.x(), vector.y());
    }

    /**
     * Creates a new Ray2D, originating from point <code>point<\code>, and going 
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public InvertedRay2D(Point2D point, double angle) {
        this(point.x(), point.y(), Math.cos(angle), Math.sin(angle));
    }

    /**
     * Creates a new Ray2D, originating from point
     * <code>(x, y)<\code>, and going 
     * in the direction specified by <code>angle<\code> (in radians).
     */
    public InvertedRay2D(double x, double y, double angle) {
        this(x, y, Math.cos(angle), Math.sin(angle));
    }

    /**
     * Creates a new Ray2D, originating from point
     * <code>(x1,y1)<\code>, and going 
     * in the direction defined by vector <code>(dx, dy)<\code>.
     */
    public InvertedRay2D(double x1, double y1, double dx, double dy) {
        super(x1, y1, dx, dy);
        
        // enforce condition on direction vector
        if (Math.hypot(dx, dy) < Shape2D.ACCURACY)
        {
            throw new IllegalArgumentException("Rays can not have direction vector with zero norm");
        }
    }


    /**
     * Define a new Ray, with same characteristics as given object.
     */
    public InvertedRay2D(LinearShape2D line) {
        super(line.origin(), line.direction());
        
        // enforce condition on direction vector
        if (Math.hypot(dx, dy) < Shape2D.ACCURACY)
        {
            throw new IllegalArgumentException("Rays can not have direction vector with zero norm");
        }
    }

    // ===================================================================
    // methods implementing the CirculinearCurve2D interface

	/**
	 * Returns another instance of InvertedRay2D, parallel to this one, 
	 * and located at the given distance.
	 * @see math.geom2d.circulinear.CirculinearCurve2D#parallel(double)
	 */
	public InvertedRay2D parallel(double d) {
        double dd = Math.hypot(dx, dy);
		return new InvertedRay2D(x0 + dy * d / dd, y0 - dx * d / dd, dx, dy);
	}

    // ===================================================================
    // methods implementing the ContinuousCurve2D interface

    /** Throws an infiniteShapeException */
    public GeneralPath appendPath(GeneralPath path) {
        throw new UnboundedShape2DException(this);
    }

    /** Throws an infiniteShapeException */
    public GeneralPath getGeneralPath() {
        throw new UnboundedShape2DException(this);
    }

    // ===================================================================
    // methods implementing the Curve2D interface

    public Point2D point(double t) {
        t = Math.min(t, 0);
		return new Point2D(x0 + t * dx, y0 + t * dy);
    }

    /**
     * Returns Negative infinity.
     */
    public double t0() {
        return Double.NEGATIVE_INFINITY;
    }

    /**
     * @deprecated replaced by t0() (since 0.11.1).
     */
    @Deprecated
    public double getT0() {
    	return t0();
    }

    /**
     * Returns 0.
     */
    public double t1() {
        return 0;
    }

    /**
     * @deprecated replaced by t1() (since 0.11.1).
     */
    @Deprecated
    public double getT1() {
    	return t1();
    }

    /**
     * Reverses this curve, and return the result as an instance of Ray2D.
     * @see Ray2D#reverse()
     */
    public Ray2D reverse() {
        return new Ray2D(x0, y0, -dx, -dy);
    }

    // ===================================================================
    // methods implementing the Shape2D interface

    /** Always returns false, because n inverted ray is not bounded. */
    public boolean isBounded() {
        return false;
    }

    public boolean contains(double x, double y) {
        if (!this.supportContains(x, y))
            return false;
        double t = this.positionOnLine(x, y);
        return t < Shape2D.ACCURACY;
    }

    public Box2D boundingBox() {
        double t = Double.NEGATIVE_INFINITY;
        Point2D p0 = new Point2D(x0, y0);
        Point2D p1 = new Point2D(t * dx, t* dy);
		return new Box2D(p0, p1);
	}

    @Override
	public InvertedRay2D transform(AffineTransform2D trans) {
		double[] tab = trans.coefficients();
		double x1 = x0 * tab[0] + y0 * tab[1] + tab[2];
		double y1 = x0 * tab[3] + y0 * tab[4] + tab[5];
		return new InvertedRay2D(x1, y1, 
				dx * tab[0] + dy * tab[1], dx * tab[3] + dy * tab[4]);
	}

    // ===================================================================
    // methods implementing the Shape interface


	// ===================================================================
	// methods implementing the GeometricObject2D interface

	/* (non-Javadoc)
	 * @see math.geom2d.GeometricObject2D#almostEquals(math.geom2d.GeometricObject2D, double)
	 */
    public boolean almostEquals(GeometricObject2D obj, double eps) {
		if (this == obj)
			return true;
    	
		if (!(obj instanceof InvertedRay2D))
			return false;
		InvertedRay2D ray = (InvertedRay2D) obj;
		if (Math.abs(x0 - ray.x0) > eps)
			return false;
		if (Math.abs(y0 - ray.y0) > eps)
			return false;
		if (Math.abs(dx - ray.dx) > eps)
			return false;
		if (Math.abs(dy - ray.dy) > eps)
			return false;

		return true;
    }

    // ===================================================================
    // methods implementing the Object interface

    @Override
    public String toString() {
        return new String("InvertedRay2D(" + x0 + "," + y0 + "," + 
        		dx + "," + dy + ")");
    }
    
    @Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof InvertedRay2D))
			return false;
		InvertedRay2D that = (InvertedRay2D) obj;
		
        // Compare each field
		if (!EqualUtils.areEqual(this.x0, that.x0)) 
			return false;
		if (!EqualUtils.areEqual(this.y0, that.y0)) 
			return false;
		if (!EqualUtils.areEqual(this.dx, that.dx)) 
			return false;
		if (!EqualUtils.areEqual(this.dy, that.dy)) 
			return false;

		return true;
    }

	/**
	 * @deprecated use copy constructor instead (0.11.2)
	 */
	@Deprecated
    @Override
    public InvertedRay2D clone() {
        return new InvertedRay2D(x0, y0, dx, dy);
    }

}