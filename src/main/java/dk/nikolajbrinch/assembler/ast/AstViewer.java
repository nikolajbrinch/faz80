package dk.nikolajbrinch.assembler.ast;

import dk.nikolajbrinch.assembler.ast.statements.Statement;
import dk.nikolajbrinch.assembler.parser.AssemblerParser;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

public class AstViewer extends JFrame {

  private static     File file = new File(new File("."), "src/test/resources/hello-world.z80");

  private final int width;
  private final int height;

  public AstViewer() {
    super("AST Viewer");

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    width = screenSize.width * 3 / 4;
    height = screenSize.height * 3 / 4;

    setSize(width, height);
    setLocationRelativeTo(null);
  }

  public static void main(String[] args) {
    AstViewer viewer = new AstViewer();

    viewer.setup();
  }

  private void setup() {
    JSplitPane splitPane = new JSplitPane();
    add(splitPane);

    try {
      JTree tree = new JTree(createTreeModel());
      expandAllNodes(tree, 0, tree.getRowCount());
      tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      JScrollPane treeScrollPane = new JScrollPane(tree);
      splitPane.setLeftComponent(treeScrollPane);

      PropertiesTableModel tableModel = new PropertiesTableModel();
      JTable propertiesTable = new JTable(tableModel);
      JScrollPane tableScrollPane = new JScrollPane(propertiesTable);
      splitPane.setRightComponent(tableScrollPane);

      tree.addTreeSelectionListener(
          new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
              DefaultMutableTreeNode selectedNode =
                  (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
              if (selectedNode != null) {
                // Here, you create and pass a map of properties based on the selected node
                Map<String, String> properties = new HashMap<>();
                // Example properties, replace with actual properties of your node
                properties.put("Name", selectedNode.toString());
                properties.put("Child Count", String.valueOf(selectedNode.getChildCount()));
                // Update the table model
                tableModel.setProperties(properties);
              }
            }
          });

      int dividerLocation = width - 400;
      splitPane.setDividerLocation(dividerLocation);
      setVisible(true);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

  }

  public void expandAllNodes(JTree tree, int startingIndex, int rowCount){
    for (int i = startingIndex; i < rowCount; ++i) {
      tree.expandRow(i);
    }

    if (tree.getRowCount() != rowCount) {
      expandAllNodes(tree, rowCount, tree.getRowCount());
    }
  }

  private static DefaultMutableTreeNode createTreeModel() throws IOException {
    List<Statement> statements = new AssemblerParser().parse(file);

    return new AstTreeCreator().createTree(statements);
  }
}

class PropertiesTableModel extends AbstractTableModel {
  private String[] columnNames = {"Property", "Value"};
  private Object[][] data = {};

  public void setProperties(Map<String, String> properties) {
    data = new Object[properties.size()][2];
    int i = 0;
    for (Map.Entry<String, String> entry : properties.entrySet()) {
      data[i][0] = entry.getKey();
      data[i][1] = entry.getValue();
      i++;
    }
    fireTableDataChanged();
  }

  @Override
  public int getRowCount() {
    return data.length;
  }

  @Override
  public int getColumnCount() {
    return columnNames.length;
  }

  @Override
  public Object getValueAt(int row, int col) {
    return data[row][col];
  }

  @Override
  public String getColumnName(int col) {
    return columnNames[col];
  }
}
