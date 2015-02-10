package org.jmeld.ui.action;

import org.jmeld.util.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Actions {
  public final Action NEW = new Action("New");
  public final Action SAVE = new Action("Save");
  public final Action UNDO = new Action("Undo");
  public final Action REDO = new Action("Redo");
  public final Action RIGHT = new Action("Right");
  public final Action LEFT = new Action("Left");
  public final Action UP = new Action("Up");
  public final Action DOWN = new Action("Down");
  public final Action ZOOM_PLUS = new Action("ZoomPlus");
  public final Action ZOOM_MIN = new Action("ZoomMin");
  public final Action GOTO_SELECTED = new Action("GoToSelected");
  public final Action GOTO_FIRST = new Action("GoToFirst");
  public final Action GOTO_LAST = new Action("GoToLast");
  public final Action GOTO_LINE = new Action("GoToLine");
  public final Action START_SEARCH = new Action("StartSearch");
  public final Action NEXT_SEARCH = new Action("NextSearch");
  public final Action PREVIOUS_SEARCH = new Action("PreviousSearch");
  public final Action REFRESH = new Action("Refresh");
  public final Action MERGEMODE = new Action("MergeMode");
  public final Action HELP = new Action("Help");
  public final Action ABOUT = new Action("About");
  public final Action SETTINGS = new Action("Settings");
  public final Action EXIT = new Action("Exit");
  public final Action FOLDER_SELECT_NEXT_ROW = new Action("SelectNextRow");
  public final Action FOLDER_SELECT_PREVIOUS_ROW = new Action("SelectPreviousRow");
  public final Action FOLDER_NEXT_NODE = new Action("NextNode");
  public final Action FOLDER_PREVIOUS_NODE = new Action("PreviousNode");
  public final Action FOLDER_OPEN_FILE_COMPARISON = new Action("OpenFileComparison");
  public final Action FOLDER_OPEN_FILE_COMPARISON_BACKGROUND = new Action("OpenFileComparisonBackground");
  public final Action FOLDER_EXPAND_ALL = new Action("ExpandAll");
  public final Action FOLDER_COLLAPSE_ALL = new Action("CollapseAll");
  public final Action FOLDER_REFRESH = new Action("Refresh");
  public final Action FOLDER_REMOVE_RIGHT = new Action("RemoveRight");
  public final Action FOLDER_REMOVE_LEFT = new Action("RemoveLeft");
  public final Action FOLDER_COPY_TO_LEFT = new Action("CopyToLeft");
  public final Action FOLDER_COPY_TO_RIGHT = new Action("CopyToRight");
  public final Action FOLDER_FILTER = new Action("Filter");

  private final List<Action> actionList = new ArrayList<Action>();

  public class Action {
    // Instance variables:
    public Option  option = new Option(true);
    private String name;

    private Action(String name)
    {
      this.name = name;
    }

    public String getName()
    {
      return name;
    }
  }

  public List<Action> getActions()
  {
    return Collections.unmodifiableList(actionList);
  }
}
