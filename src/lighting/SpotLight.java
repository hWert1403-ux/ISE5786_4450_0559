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

}
