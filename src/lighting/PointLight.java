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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getIntensity(Point p) {
		// TODO Auto-generated method stub
		return null;
	}

}
