package life.genny.qwanda.model;

import java.io.Serializable;
import life.genny.qwanda.entity.BaseEntity;

public class Setup implements Serializable {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private BaseEntity user;
  private String layout;

  public Setup() {

  }

  /**
   * @return the user
   */
  public BaseEntity getUser() {
    return user;
  }

  /**
   * @param user the user to set
   */
  public void setUser(final BaseEntity user) {
    this.user = user;
  }

  /**
   * @return the layout
   */
  public String getLayout() {
    return layout;
  }

  /**
   * @param layout the layout to set
   */
  public void setLayout(final String layout) {
    this.layout = layout;
  }



}
