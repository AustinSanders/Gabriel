//!!(C)!!
package c2.fw;

/**
 * Describes objects that are C2 components.  A C2 component is a C2 <code>Brick</code>
 * with at most one connector welded to its top and at most one welded to its bottom.
 * Components cannot be directly welded to one another, and a component is not supposed
 * to be disconnected from the brick graph.
 *
 * @see c2.fw.Brick
 * @see c2.fw.AbstractBrick
 * @author Eric M. Dashofy <A HREF="mailto:edashofy@ics.uci.edu">edashofy@ics.uci.edu</A>
 */
public interface Component extends Brick{

}

