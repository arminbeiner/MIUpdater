package models;

/** ein customer result objekt um DB ergebnisse zu laden
 * Created by asi on 14.01.2017.
 */
public class CustomerResult {

    private String name;
    private String hostname;
    private String ip;
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * lädt ein customer result in ein string array um den jtable zu laden
     * @param customerResult customer result
     * @return string array
     */
    public String[] loadCustomerResultToArray(CustomerResult customerResult) {
        String[] result = new String[4];
        result[0] = customerResult.getName();
        result[1] = customerResult.getHostname();
        result[2] = customerResult.getIp();
        result[3] = customerResult.getType();
        return result;
    }
}
