package org.lilu.sns.service;

import org.lilu.sns.dao.LoginTicketDao;
import org.lilu.sns.exception.EntityUpdateException;
import org.lilu.sns.pojo.LoginTicket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Auther: lilu
 * @Date: 2019/1/26
 * @Description: 操作loginTicket的业务类
 */
@Service
public class LoginTicketService {
    @Autowired
    private LoginTicketDao loginTicketDao;

    /**
     * 给传入的userId下发对应ticket，如果已经存在未过期的ticket，则使用已经存在的，否则生成新的。
     * @param userId
     * @return
     */
    public String addLoginTicket(int userId) {
        // 获取userId对应的有效的loginTicket
        LoginTicket loginTicket = getValidLoginTicket(userId);
        if (loginTicket != null) {
            return loginTicket.getTicket();
        } else {
            // 否则给给userId生成一个新的ticket插入数据库。
            loginTicket = new LoginTicket();
            loginTicket.setUserId(userId);
            loginTicket.setTicket(UUID.randomUUID().toString().replaceAll("-",""));
            Date date = new Date();
            // 设置过期时间为1个小时，单位毫秒。
            date.setTime(date.getTime() + 1000 * 3600 * 24);
            loginTicket.setExpired(date);
            loginTicket.setStatus(0);
            // 向数据库中写入loginTicket
            if (loginTicketDao.insert(loginTicket) != 1) {
                throw new EntityUpdateException("异常：添加loginTicket异常");
            }
            return loginTicket.getTicket();
        }
    }

    /**
     * 让ticket失效：status为1则ticket失效
     * @param ticket
     * @return 返回影响的记录行数
     */
    public int invalidate(String ticket) {
        return loginTicketDao.updateStatus(ticket,1);
    }

    /**
     * 获取ticket对应的loginTicket对象。
     * @param ticket
     * @return 返回一个LoginTicket对象，无论对应的ticket是否为空或者是否有效。
     */
    public LoginTicket getLoginTicket(String ticket) {
        return loginTicketDao.selectByTicket(ticket);
    }

    /**
     * 获取userID对应的有效的ticket
     * @param userId
     * @return 返回数据库中有效的ticket，如果数据库中没有则返回null
     */
    public LoginTicket getValidLoginTicket(int userId) {
        LoginTicket loginTicket = null;
        List<LoginTicket> loginTickets = loginTicketDao.selectByUserId(userId);
        if (loginTickets != null) {
            // 迭代集合，如果存在ticket有效的情况，则赋给loginTicket，中止循环。
            for (LoginTicket ticket : loginTickets) {
                if (checkValidity(ticket)) {
                    loginTicket = ticket;
                    break;
                }
            }
        }
        return loginTicket;
    }

    /**
     * 抽取检验ticket有效性的方法
     * @param loginTicket
     * @return ticket有效返回true，无效返回false。
     */
    public boolean checkValidity(LoginTicket loginTicket) {
        // ticket无效的情况：expired过期或status不为0
        return !(loginTicket == null || loginTicket.getExpired().before(new Date()) || loginTicket.getStatus() != 0);
    }
}