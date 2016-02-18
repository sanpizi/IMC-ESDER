package com.zyt.user;

import com.zyt.SqlStmts;
import com.zyt.Util;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Administrator on 2015/8/15.
 */
public class UserDetails {
    private static final Logger logger = LogManager.getLogger(UserDetails.class);

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getClerkName() {
        return clerkName;
    }

    public void setClerkName(String clerkName) {
        this.clerkName = clerkName;
    }

    public int getClerkType() {
        return clerkType;
    }

    public void setClerkType(int clerkType) {
        this.clerkType = clerkType;
    }

    public boolean isAllowSMS() {
        return allowSMS;
    }

    public void setAllowSMS(boolean allowSMS) {
        this.allowSMS = allowSMS;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGsm_phone() {
        return gsm_phone;
    }

    public void setGsm_phone(String gsm_phone) {
        this.gsm_phone = gsm_phone;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public String getEmailAddr() {
        return emailAddr;
    }

    public void setEmailAddr(String emailAddr) {
        this.emailAddr = emailAddr;
    }

    public int getAllowEmail() {
        return allowEmail;
    }

    public void setAllowEmail(int allowEmail) {
        this.allowEmail = allowEmail;
    }

    public int getAllowJob() {
        return allowJob;
    }

    public void setAllowJob(int allowJob) {
        this.allowJob = allowJob;
    }

    public int getAllowPhone() {
        return allowPhone;
    }

    public void setAllowPhone(int allowPhone) {
        this.allowPhone = allowPhone;
    }

    private int ID = -1;
    private String clerkName;
    private int clerkType;
    private boolean allowSMS;
    private String password;
    private String gsm_phone;
    private String contact;
    private String address;
    private String memo;
    private String emailAddr;
    private int allowEmail;
    private int allowJob;
    private int allowPhone;

    public UserDetails(Connection conn, int ID) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            logger.debug("start querying user details");
            stmt = conn.prepareStatement(SqlStmts.SELECT_USER_DETAILS_BASED_ON_ID);
            stmt.setInt(1, ID);
            rs = stmt.executeQuery();
            if (rs.next()) {
                this.ID = rs.getInt(1);
                this.clerkName = rs.getString(2);
                this.clerkType = rs.getInt(3);
                this.allowSMS = rs.getBoolean(4);
                this.password = rs.getString(5);
                this.gsm_phone = rs.getString(6);
                this.contact = rs.getString(7);
                this.address = rs.getString(8);
                this.memo = rs.getString(9);
                this.emailAddr = rs.getString(10);
                this.allowEmail = rs.getInt(11);
                this.allowJob = rs.getInt(12);
                this.allowPhone = rs.getInt(13);
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying user details");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public UserDetails(Connection conn, String clerkName) {
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            logger.debug("start querying user details");
            stmt = conn.prepareStatement(SqlStmts.SELECT_USER_DETAILS_BASED_ON_NAME);
            stmt.setString(1, clerkName);
            rs = stmt.executeQuery();
            if (rs.next()) {
                this.ID = rs.getInt(1);
                this.clerkName = rs.getString(2);
                this.clerkType = rs.getInt(3);
                this.allowSMS = rs.getBoolean(4);
                this.password = rs.getString(5);
                this.gsm_phone = rs.getString(6);
                this.contact = rs.getString(7);
                this.address = rs.getString(8);
                this.memo = rs.getString(9);
                this.emailAddr = rs.getString(10);
                this.allowEmail = rs.getInt(11);
                this.allowJob = rs.getInt(12);
                this.allowPhone = rs.getInt(13);
            }
            Util.safeClose(rs, stmt);
            logger.debug("end of querying user details");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            Util.safeClose(rs, stmt);
        }
    }

    public String getClerkTypeStr() {
        switch (clerkType) {
            case 0:
                return "Engineer";
            case 1:
                return "Admin";
            case 2:
                return "Guest";
            case 4:
                return "Operator";
            default:
                return "None";
        }
    }
}
