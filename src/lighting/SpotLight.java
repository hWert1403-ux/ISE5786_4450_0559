package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class SpotLight extends PointLight {

	private final Vector _direction;

	public SpotLight(Color intensity, Point p, Vector direction) {
		super(intensity, p);
		_direction = direction.normalize();
	}

	@Override
	public Vector getL(Point p) {
		return super.getL(p);
	}

	@Override
	public Color getIntensity(Point p) {
		double cosTheta = _direction.dotProduct(getL(p));

		// if point behind spotLight or out of 90 degrees spot
		if (cosTheta <= 0) {
			return Color.BLACK;
		}

		return super.getIntensity(p).scale(cosTheta);
	}

	@Override
	public SpotLight setKc(double kC) {
		super.setKc(kC);
		return this;
	}

	@Override
	public SpotLight setKl(double kL) {
		super.setKl(kL);
		return this;
	}

	@Override
	public SpotLight setKQ(double kQ) {
		super.setKQ(kQ);
		return this;
	}

	@Override
	public double getDistance(Point point) {
		return super.getDistance(point);
	}
}
