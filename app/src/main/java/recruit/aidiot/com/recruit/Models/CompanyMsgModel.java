package recruit.aidiot.com.recruit.Models;

/**
 * Created by E on 11/9/2018.
 */

public class CompanyMsgModel extends Object {
    public int id;
    public int session_id;
    public String from_to;
    public String message;
    public String status;
    public SessionModel session;
}
