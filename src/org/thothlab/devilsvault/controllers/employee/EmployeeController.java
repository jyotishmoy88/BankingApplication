package org.thothlab.devilsvault.controllers.employee;

import java.util.HashMap;
import java.util.List;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.thothlab.devilsvault.dao.dashboard.PendingStatisticsDao;
import org.thothlab.devilsvault.dao.pendingregistration.PendingRegistrationDaoImpl;
import org.thothlab.devilsvault.dao.request.ExternalRequestDaoImpl;
import org.thothlab.devilsvault.dao.request.InternalRequestDaoImpl;
import org.thothlab.devilsvault.dao.transaction.InternalTransactionDaoImpl;
import org.thothlab.devilsvault.db.model.PendingRegistration;
import org.thothlab.devilsvault.db.model.Request;
import org.thothlab.devilsvault.db.model.Transaction;

@Controller
public class EmployeeController {
	
	@RequestMapping("/employee/userdetails")
	public ModelAndView UserDetailsContoller(){
		ModelAndView model = new ModelAndView("employeePages/employeeUserDetails");
		model.addObject("request_list","test message");
		return model;
	}
	
	@RequestMapping("/employee/transaction")
	public ModelAndView TransactionContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        InternalTransactionDaoImpl transactionDAO = ctx.getBean("TransactionSpecificDao", InternalTransactionDaoImpl.class);
        List <Transaction> complete_list = transactionDAO.getAllCompleted();
        List <Transaction> pending_list = transactionDAO.getAllPending();
        if(complete_list.size() > 10)
            complete_list = complete_list.subList(complete_list.size() - 10, complete_list.size());
        if(pending_list.size() > 10)
            pending_list = pending_list.subList(pending_list.size() - 10, pending_list.size());
        ModelAndView model = new ModelAndView("employeePages/employeeTransaction");
        model.addObject("complete_list",complete_list);
        model.addObject("pending_list",pending_list);
        ctx.close();
        return model;
	}
	
	@RequestMapping(value="/employee/transactionsearch", method = RequestMethod.POST)
    public ModelAndView TransactionSearch(@RequestParam("transactionID") String transactionID, @RequestParam("accNo") String accNo) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        InternalTransactionDaoImpl transactionDAO = ctx.getBean("TransactionSpecificDao", InternalTransactionDaoImpl.class);
       List<Transaction> transactionCompleteList = null;
       List <Transaction> transactionPendingList = null;
       if(transactionID.length() == 0) {
           transactionPendingList = transactionDAO.getByUserId(Integer.parseInt(accNo), "transaction_pending");
           transactionCompleteList = transactionDAO.getByUserId(Integer.parseInt(accNo), "transaction_completed");

       } else {
           transactionPendingList = transactionDAO.getById(Integer.parseInt(transactionID), "transaction_pending");
           transactionCompleteList = transactionDAO.getByUserId(Integer.parseInt(transactionID), "transaction_completed");

       }
       ModelAndView model = new ModelAndView("employeePages/employeeTransaction");
       model.addObject("complete_list",transactionCompleteList);
       model.addObject("pending_list",transactionPendingList);
       ctx.close();
       return model;
    }
	
	@RequestMapping(value="/employee/transactionapprove", method = RequestMethod.POST)
    public ModelAndView TransactionApproveContoller(@RequestParam("transactionID") String transactionID){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        InternalTransactionDaoImpl transactionDAO = ctx.getBean("TransactionSpecificDao", InternalTransactionDaoImpl.class);
        transactionDAO.approveTransaction(Integer.parseInt(transactionID), "transaction_pending");
        ModelAndView model = new ModelAndView("redirect:" + "/employee/transaction");
        model.addObject("error_msg","Transaction Approved!");
       ctx.close();
       return model;
    }
    
    @RequestMapping(value="/employee/transactionreject", method = RequestMethod.POST)
    public ModelAndView TransactionRejectContoller(@RequestParam("transactionID") String transactionID){
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        InternalTransactionDaoImpl transactionDAO = ctx.getBean("TransactionSpecificDao", InternalTransactionDaoImpl.class);
        transactionDAO.rejectTransaction(Integer.parseInt(transactionID), "transaction_pending");
        ModelAndView model = new ModelAndView("redirect:" + "/employee/transaction");
        model.addObject("error_msg","Transaction Rejected!");
       ctx.close();
       return model;
    }
	
	@RequestMapping("/employee/pendingrequest")
	public ModelAndView PendingRequestContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
        List<Request> externalRequestList = externalRequestDao.getAllPending();
        if(externalRequestList.size() > 10)
            externalRequestList = externalRequestList.subList(externalRequestList.size()-10, externalRequestList.size());
        List<Request> internalRequestList = internalRequestDao.getAllPending();
        if(internalRequestList.size() > 10)
            internalRequestList = internalRequestList.subList(internalRequestList.size()-10, internalRequestList.size());
        ModelAndView model = new ModelAndView("employeePages/PendingRequest");
        model.addObject("internal_list",internalRequestList);
        model.addObject("external_list",externalRequestList);
        ctx.close();
        return model;
	}
	
	@RequestMapping("/employee/pendingrequest/approve")
	public ModelAndView PendingRequestApproveContoller(@RequestParam("requestID") String requestID, @RequestParam("requestType") String requestType){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ModelAndView model = new ModelAndView("employeePages/PendingRequest");
		if(requestType.equals("internal")) {
	        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
	        internalRequestDao.approveRequest(Integer.parseInt(requestID), requestType);
		}
		else {
			ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
			externalRequestDao.approveRequest(Integer.parseInt(requestID), requestType);
		} 
        model.addObject("error_msg","Request Approved!");
        ctx.close();
        return model;
	}
	
	@RequestMapping("/employee/pendingrequest/reject")
	public ModelAndView PendingRequestRejectContoller(@RequestParam("requestID") String requestID, @RequestParam("requestType") String requestType){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ModelAndView model = new ModelAndView("employeePages/PendingRequest");
		if(requestType.equals("internal")) {
	        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
	        internalRequestDao.rejectRequest(Integer.parseInt(requestID), requestType);
		}
		else {
			ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
			externalRequestDao.rejectRequest(Integer.parseInt(requestID), requestType);
		} 
        model.addObject("error_msg","Request Rejected!");
        ctx.close();
        return model;
	}
	
	@RequestMapping("/employee/completedrequest")
	public ModelAndView CompletedRequestContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
        List<Request> externalRequestList = externalRequestDao.getAllCompleted();
        if(externalRequestList.size() > 10)
            externalRequestList = externalRequestList.subList(externalRequestList.size()-10, externalRequestList.size());
        List<Request> internalRequestList = internalRequestDao.getAllCompleted();
        if(internalRequestList.size() > 10)
            internalRequestList = internalRequestList.subList(internalRequestList.size()-10, internalRequestList.size());
        ModelAndView model = new ModelAndView("employeePages/CompleteRequest");
        model.addObject("internal_list",internalRequestList);
        model.addObject("external_list",externalRequestList);
        ctx.close();
        return model;
	}
	
	@RequestMapping(value="/employee/pendingrequestsearch", method = RequestMethod.POST)
	public ModelAndView PendingRequestSearch(@RequestParam("requestID") String requestID, @RequestParam("userID") String userID) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
        List<Request> externalRequestList = null;
        List<Request> internalRequestList = null;
        if(requestID.length() == 0) {
        	externalRequestList = externalRequestDao.getByUserId(Integer.parseInt(userID),"pending");
        	internalRequestList = internalRequestDao.getByUserId(Integer.parseInt(userID),"pending");
        } else {
        	externalRequestList = externalRequestDao.getById(Integer.parseInt(requestID),"pending");
        	internalRequestList = internalRequestDao.getById(Integer.parseInt(requestID),"pending");
        }
        ModelAndView model = new ModelAndView("employeePages/PendingRequest");
        model.addObject("internal_list",internalRequestList);
        model.addObject("external_list",externalRequestList);
        ctx.close();
        return model;
	}
	
	
	
	@RequestMapping(value="/employee/completedrequestsearch", method = RequestMethod.POST)
	public ModelAndView CompletedRequestSearch(@RequestParam("requestID") String requestID, @RequestParam("userID") String userID) {
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
        ExternalRequestDaoImpl externalRequestDao = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
        InternalRequestDaoImpl internalRequestDao = ctx.getBean("internalRequestDao", InternalRequestDaoImpl.class);
        List<Request> externalRequestList = null;
        List<Request> internalRequestList = null;
        if(requestID.length() == 0) {
        	externalRequestList = externalRequestDao.getByUserId(Integer.parseInt(userID),"completed");
        	internalRequestList = internalRequestDao.getByUserId(Integer.parseInt(userID),"completed");
        } else {
        	externalRequestList = externalRequestDao.getById(Integer.parseInt(requestID),"completed");
        	internalRequestList = internalRequestDao.getById(Integer.parseInt(requestID),"completed");
        }
        ModelAndView model = new ModelAndView("employeePages/CompleteRequest");
        model.addObject("internal_list",internalRequestList);
        model.addObject("external_list",externalRequestList);
        ctx.close();
        return model;
	}
	
	@RequestMapping("/employee/pendingregistration")
	public ModelAndView PendingRegistrationContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		PendingRegistrationDaoImpl pendingRegistrationDao = ctx.getBean("PendingRegistrationDao", PendingRegistrationDaoImpl.class);
		List<PendingRegistration> PendingRegistrationList = pendingRegistrationDao.getAllPending();
        ModelAndView model = new ModelAndView("employeePages/PendingRegistration");
        model.addObject("registration_list",PendingRegistrationList);
        ctx.close();
        return model;
	}
	
	
	@RequestMapping("/employee/home")
	public ModelAndView PendingDashboardContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		PendingStatisticsDao pendingStatisticsDao = ctx.getBean("pendingStatistics", PendingStatisticsDao.class);
		HashMap<String,Integer> stats = new HashMap<String,Integer>();
		stats = pendingStatisticsDao.getPendingStatistics();
		List<Request> internal_list = pendingStatisticsDao.getPendingInternalRequests();
		if(internal_list.size() > 5)
			internal_list = internal_list.subList(internal_list.size()-5, internal_list.size());
		List<Request> external_list = pendingStatisticsDao.getPendingExternalRequests();
		if(external_list.size() > 5)
			external_list = external_list.subList(external_list.size()-5, external_list.size());
		List<PendingRegistration> user_list = pendingStatisticsDao.getPendingUserRegistrations();
		if(user_list.size() > 5)
			user_list = user_list.subList(user_list.size()-5, user_list.size());
		List<Transaction> transaction_list = pendingStatisticsDao.getPendingTransactions();
		if(transaction_list.size() > 5)
			transaction_list = transaction_list.subList(transaction_list.size()-5, transaction_list.size());
		ModelAndView model = new ModelAndView("employeePages/employeeDashboard");
		model.addObject("stats",stats);
		model.addObject("internal_list",internal_list);
		model.addObject("external_list",external_list);
		model.addObject("user_list",user_list);
		model.addObject("transaction_list",transaction_list);
		ctx.close();
		return model;
	}
	
	@RequestMapping("/employee/openrequests")
	public ModelAndView OpenRequestContoller(){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExternalRequestDaoImpl requestDAO = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
		List<Request> requestList = requestDAO.getAllPending();
		ModelAndView model = new ModelAndView("employeePages/employeeRequest");
		model.addObject("request_list",requestList);
		ctx.close();
		return model;
	}
		
	@RequestMapping("/employee/createrequest")
	public ModelAndView CreateRequestContoller(Request request){
		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("jdbc/config/DaoDetails.xml");
		ExternalRequestDaoImpl requestDAO = ctx.getBean("externalRequestDao", ExternalRequestDaoImpl.class);
		boolean result = requestDAO.save(request, "external");
		ModelAndView model = new ModelAndView("employeePages/employeeRequest");
		if (result)
			model.addObject("msg","Request Created");
		else
			model.addObject("msg","Request Failed");
		ctx.close();
		return model;
	}
}