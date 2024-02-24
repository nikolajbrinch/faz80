package dk.nikolajbrinch.faz80.parser.cst.blocks;

import dk.nikolajbrinch.faz80.parser.cst.CompositeLineNode;
import dk.nikolajbrinch.faz80.parser.cst.LineNode;
import dk.nikolajbrinch.faz80.parser.cst.NodeType;
import java.util.ArrayList;
import java.util.List;

public interface BlockNode extends CompositeLineNode {
  BlockType blockType();

  LineNode startLine();

  CompositeLineNode body();

  LineNode endLine();

  @Override
  default NodeType type() {
    return NodeType.BLOCK;
  }

  @Override
  default List<LineNode> lines() {
    List<LineNode> lines = new ArrayList<>();
    lines.add(startLine());
    lines.addAll(body().lines());
    lines.add(endLine());

    return lines;
  }
}
