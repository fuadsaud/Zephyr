package zephyr.serialization;

import java.awt.Color;

import flexjson.transformer.AbstractTransformer;

public class ColorHEXTransformer extends AbstractTransformer {

	@Override
	public void transform(Object object) {
		Color c = (Color) object;
		getContext().write(
				"#" + Integer.toHexString(c.getRed()) + Integer.toHexString(c.getGreen())
						+ Integer.toHexString(c.getBlue()));
	}
}
