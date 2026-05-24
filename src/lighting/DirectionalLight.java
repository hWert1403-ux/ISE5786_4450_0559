package lighting;

import primitives.Color;
import primitives.Point;
import primitives.Vector;

public class DirectionalLight extends Light implements LightSource {

	private final Vector _direction;

	public DirectionalLight(Color intensity, Vector direction) {
		super(intensity);
		_direction = direction.normalize();
	}

	@Override
	public Vector getL(Point p) {
		return _direction;
	}

	@Override
	public Color getIntensity(Point p) {
		return getIntensity();
	}

	@Override
	public double getDistance(Point point) {
		return Double.POSITIVE_INFINITY;
	}

}
