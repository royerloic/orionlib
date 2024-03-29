/*
 * Drej
 * Copyright (c) 2005 Greg Dennis
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package utils.math.regression;

import javax.vecmath.GVector;

/**
 * A multiquadric {@link com.gregdennis.drej.Kernel kernel} of the following
 * form: <br>
 * 
 * <blockquote> K(x1, x2) = 1 / &radic;(&#8741;x1 - x2&#8741;&sup2; +
 * &gamma;&sup2;) </blockquote>
 * 
 * @author Greg Dennis (gdennis@mit.edu)
 */
public final class InverseMultiquadricKernel implements Kernel
{

	private final double gamma, a;

	/**
	 * Constructs an inverse multiquadric kernel with the specified value for
	 * &gamma;.
	 */
	public InverseMultiquadricKernel(final double gamma)
	{
		this.gamma = gamma;
		a = gamma * gamma;
	}

	/**
	 * Returns the value of &gamma;.
	 */
	public double gamma()
	{
		return gamma;
	}

	public double eval(final GVector x1, final GVector x2)
	{
		return 1 / Math.sqrt(Matrices.distanceSquared(x1, x2) + a);
	}

}
