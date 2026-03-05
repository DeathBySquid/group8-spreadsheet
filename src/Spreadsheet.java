import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Spreadsheet {
    private Cell[][] cells;
    private DependencyGraph graph = new DependencyGraph();

    public Spreadsheet(int size) {
        cells = new Cell[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                cells[i][j] = new Cell();
            }
        }
    }

    public int getNumRows() {
        return cells.length;
    }

    public int getNumColumns() {
        return cells[0].length;
    }

    public Cell getCell(CellToken token) {
        return cells[token.getRow()][token.getColumn()];
    }

    public int getCellValue(int row, int column) {
        return cells[row][column].getValue();
    }

    public Cell getCell(String cellName) {
        CellToken token = new CellToken();
        SpreadsheetUtils.getCellToken(cellName, 0, token);
        return cells[token.getRow()][token.getColumn()];
    }


    public void changeCellFormulaAndRecalculate(CellToken token, String formula, Stack<Token> postfix) {
        Cell cell = getCell(token);

        cell.setFormula(formula);
        cell.buildExpressionTree(postfix);
        List<Cell> refs = extractReferences(postfix);

        graph.clearDependencies(cell);
        for (Cell ref : refs) {
            graph.addDependencies(ref, cell);

        }

        List<Cell> order = topoSort(graph);

        for (Cell c : order) {
            c.evaluate(this);
        }
    }

    public void printValues() {
        for (Cell[] cell : cells) {
            for (Cell value : cell) {
                System.out.print(value.getValue());
            }
        }
    }

    public void printCellFormula(CellToken token) {
        Cell cell = getCell(token);
        System.out.print(cell.getFormula());
    }

    public void printAllFormulas() {
        for (Cell[] cell : cells) {
            for (Cell value : cell) {
                System.out.print(value.getFormula());
            }
        }
    }

    private List<Cell> extractReferences(Stack<Token> postfix) {
        List<Cell> refs = new ArrayList<>();
        for (Token t : postfix) {
            if (t instanceof CellToken token) {
                int row = token.getRow();
                int column = token.getColumn();
                refs.add(cells[row][column]);
            }
        }
        return refs;
    }
}
