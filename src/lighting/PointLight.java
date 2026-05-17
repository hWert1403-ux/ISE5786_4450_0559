package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class PointLight extends Light implements LightSource {

	protected final Point _position;
	private double _kC = 1d, _kL = 0d, _kQ = 0d;

	public PointLight(Color intensity, Point position) {
		super(intensity);
		_position = position;
	}

	@Override
	public Vector getL(Point p) {
		return p.subtract(_position).normalize();
	}

	@Override
	public Color getIntensity(Point p) {
		double dSquared = _position.distanceSquared(p);
		double d = _position.distance(p);

		double factor = _kC + _kL * d + _kQ * dSquared; // the formula

		return getIntensity().scale(1d / factor);
	}

	public PointLight setKc(double kC) {
		_kC = kC;
		return this;
	}

	public PointLight setKl(double kL) {
		_kL = kL;
		return this;
	}

	public PointLight setKQ(double kQ) {
		_kQ = kQ;
		return this;
	}

}
