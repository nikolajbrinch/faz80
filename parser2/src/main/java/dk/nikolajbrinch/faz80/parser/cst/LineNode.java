package dk.nikolajbrinch.faz80.parser.cst;

import dk.nikolajbrinch.faz80.parser.cst.blocks.BlockNode;
import dk.nikolajbrinch.faz80.parser.cst.conditional.ConditionalNode;

public sealed interface LineNode extends Node
    permits BasicLineNode, BlockNode, ConditionalNode, LinesNode {}
