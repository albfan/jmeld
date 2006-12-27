package org.jmeld.ui.search;

public class SearchCommand
{
  private String  searchText;
  private boolean regularExpression;

  public SearchCommand(
    String  searchText,
    boolean regularExpression)
  {
    this.searchText = searchText;
    this.regularExpression = regularExpression;
  }

  public String getSearchText()
  {
    return searchText;
  }

  public boolean isRegularExpression()
  {
    return regularExpression;
  }
}
