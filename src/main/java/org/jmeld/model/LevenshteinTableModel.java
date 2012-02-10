package org.jmeld.model;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

/**
 * Modelo de levenstein
 * User: alberto
 * Date: 7/12/12
 * Time: 16:40
 */
public class LevenshteinTableModel extends DefaultTableModel {
    String origin;
    String destiny;

    public LevenshteinTableModel(String origin, String destiny) {
        super();
        this.origin = origin;
        this.destiny = destiny;
        buildModel();
    }

    private void buildModel() {
        setDataVector(newVector(origin.length() + 2), newVector(destiny.length() + 2));
        int rowCount = getRowCount();
        int columnCount = getColumnCount();

        for (int columna = 0; columna < columnCount; columna++) {
            if (columna < 2) {
                setValueAt("", 0, columna);
            } else {
                setValueAt(destiny.charAt(columna - 2), 0, columna);
            }
        }

        for (int fila = 0; fila < rowCount; fila++) {
            if (fila < 2) {
                setValueAt("", fila, 0);
            } else {
                setValueAt(origin.charAt(fila - 2), fila, 0);
            }
        }

        int minLength = rowCount > columnCount ? columnCount : rowCount;

        for (int diagonal = 1; diagonal < minLength; diagonal++) {

            if (diagonal == 1) {
                setValueAt(0, diagonal, diagonal);
            } else {
                Object o = getValueAt(diagonal - 1, diagonal - 1);
                int incremento;

                if (origin.charAt(diagonal - 2) == destiny.charAt(diagonal - 2)) {
                    incremento = 0;
                } else {
                    incremento = 1;
                }

                setValueAt((Integer) o + incremento, diagonal, diagonal);
            }

            for (int columna = diagonal + 1; columna < columnCount; columna++) {
                setValueAt((Integer) getValueAt(diagonal, columna - 1) + 1, diagonal, columna);
            }

            for (int fila = diagonal + 1; fila < rowCount; fila++) {
                setValueAt((Integer) getValueAt(fila - 1, diagonal) + 1, fila, diagonal);
            }
        }
    }

    private Vector newVector(int size) {
        Vector vector = new Vector(size);
        vector.setSize(size);
        return vector;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
        buildModel();
    }

    public String getDestiny() {
        return destiny;
    }

    public void setDestiny(String destiny) {
        this.destiny = destiny;
        buildModel();
    }

    public DefaultTableCellRenderer getCellRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component tableCellRendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Color background = Color.WHITE;
                if (row < 2 && column < 2) {
                    if (row != 1 || column != 1) {
                        background = Color.BLUE;
                    } else {
                        background = Color.GREEN;
                    }
                } else {
                    if (row > 1) {
                        if (column > 1) {
                            if (origin.charAt(row - 2) == destiny.charAt(column - 2)) {
                                background = Color.ORANGE;
                            }
                        }
                    }

                    if (row == 0 || column == 0) {
                        background = new Color(0x550099FF, true);
                    }
                }
                if (isSelected) {
                    Color newColor = Color.CYAN;
                    Color mixColor = Color.WHITE;
                    background = new Color(background.getRed() * newColor.getRed()/mixColor.getRed()
                    ,background.getGreen() * newColor.getGreen()/mixColor.getGreen()
                            ,background.getBlue() * newColor.getBlue()/mixColor.getBlue());
                }
                tableCellRendererComponent.setBackground(background);
                ((JLabel) tableCellRendererComponent).setHorizontalAlignment(SwingConstants.CENTER);
                return tableCellRendererComponent;
            }
        };
    }
}
