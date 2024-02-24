package dk.nikolajbrinch.faz80.parser.cst;

import java.util.List;

public interface CompositeLineNode extends LineNode {

  List<LineNode> lines();

}
