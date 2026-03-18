package com.github.mreep.changelistrouter.settings;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vcs.changes.ChangeListManager;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChangelistRouterSettingsPanel
{

    private final Project project;
    private final List<RouteMapping> mappings = new ArrayList<>();
    private final RouteMappingTableModel tableModel = new RouteMappingTableModel();
    private final JBTable table = new JBTable(this.tableModel);
    private final JComponent component;

    public ChangelistRouterSettingsPanel(Project project)
    {
        this.project = project;
        this.table.setShowGrid(true);
        this.table.setRowHeight(28);

        // Type column editor
        ComboBox<PatternType> typeCombo = new ComboBox<>(PatternType.values());
        this.table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(typeCombo));

        // Changelist Name column editor — populate changelists when editing begins
        ComboBox<String> changelistCombo = new ComboBox<>();
        changelistCombo.setEditable(true);

        this.table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(changelistCombo) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column)
            {
                String currentText = value instanceof String s ? s : "";

                changelistCombo.removeAllItems();

                ChangeListManager.getInstance(ChangelistRouterSettingsPanel.this.project).getChangeLists().forEach(
                    cl -> changelistCombo.addItem(cl.getName())
                );

                changelistCombo.getEditor().setItem(currentText);

                return super.getTableCellEditorComponent(table, currentText, isSelected, row, column);
            }
        });

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(this.table)
            .setAddAction(button -> {
                this.mappings.add(new RouteMapping("", ""));
                this.tableModel.fireTableRowsInserted(this.mappings.size() - 1, this.mappings.size() - 1);
            })
            .setRemoveAction(button -> {
                int selected = this.table.getSelectedRow();
                if (selected >= 0) {
                    this.mappings.remove(selected);
                    this.tableModel.fireTableRowsDeleted(selected, selected);
                }
            })
            .setMoveUpAction(button -> {
                int selected = this.table.getSelectedRow();
                if (selected > 0) {
                    Collections.swap(this.mappings, selected, selected - 1);
                    this.tableModel.fireTableRowsUpdated(selected - 1, selected);
                    this.table.setRowSelectionInterval(selected - 1, selected - 1);
                }
            })
            .setMoveDownAction(button -> {
                int selected = this.table.getSelectedRow();
                if (selected >= 0 && selected < this.mappings.size() - 1) {
                    Collections.swap(this.mappings, selected, selected + 1);
                    this.tableModel.fireTableRowsUpdated(selected, selected + 1);
                    this.table.setRowSelectionInterval(selected + 1, selected + 1);
                }
            });

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(decorator.createPanel(), BorderLayout.CENTER);

        this.component = panel;
    }

    public JComponent getComponent()
    {
        return this.component;
    }

    public List<RouteMapping> getMappings()
    {
        List<RouteMapping> copy = new ArrayList<>();

        for (RouteMapping m : this.mappings) {
            copy.add(new RouteMapping(m.getPattern(), m.getChangelistName(), m.getPatternType()));
        }

        return copy;
    }

    public void setMappings(List<RouteMapping> newMappings)
    {
        this.mappings.clear();

        for (RouteMapping m : newMappings) {
            this.mappings.add(new RouteMapping(m.getPattern(), m.getChangelistName(), m.getPatternType()));
        }

        this.tableModel.fireTableDataChanged();
    }

    private class RouteMappingTableModel extends AbstractTableModel
    {

        private final String[] columnNames = {"Type", "Pattern", "Changelist Name"};

        @Override
        public int getRowCount()
        {
            return ChangelistRouterSettingsPanel.this.mappings.size();
        }

        @Override
        public int getColumnCount()
        {
            return 3;
        }

        @Override
        public String getColumnName(int column)
        {
            return this.columnNames[column];
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex)
        {
            return true;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex)
        {
            RouteMapping mapping = ChangelistRouterSettingsPanel.this.mappings.get(rowIndex);

            return switch (columnIndex) {
                case 0 -> mapping.getPatternType();
                case 1 -> mapping.getPattern();
                case 2 -> mapping.getChangelistName();
                default -> "";
            };
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex)
        {
            RouteMapping mapping = ChangelistRouterSettingsPanel.this.mappings.get(rowIndex);

            switch (columnIndex) {
                case 0 -> mapping.setPatternType(aValue instanceof PatternType pt ? pt : PatternType.REGEX);
                case 1 -> mapping.setPattern(aValue instanceof String s ? s : "");
                case 2 -> mapping.setChangelistName(aValue instanceof String s ? s : "");
            }

            fireTableCellUpdated(rowIndex, columnIndex);
        }
    }
}
