package org.jmeld.ui.action;

import java.util.*;

public class ActionHandler
{
  private Map<String, MeldAction> actions = new HashMap<String, MeldAction>();

  public ActionHandler()
  {
  }

  public MeldAction get(String name)
  {
    return actions.get(name);
  }

  public MeldAction createAction(Object object, String name)
  {
    MeldAction action;

    action = new MeldAction(this, object, name);
    actions.put(name, action);

    checkActions();

    return action;
  }

  public void checkActions()
  {
    boolean actionEnabled;
    boolean someActionChanged;

    do
    {
      someActionChanged = false;
      for (MeldAction action : actions.values())
      {
        actionEnabled = action.isActionEnabled();
        if (actionEnabled != action.isEnabled())
        {
          action.setEnabled(actionEnabled);

          // Some actions depend on other actions!
          someActionChanged = true;
        }
      }
    }
    while (someActionChanged);
  }
}
