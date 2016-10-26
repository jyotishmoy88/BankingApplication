<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@include file="employeeHeader.jsp" %>

	<div class="content-wrapper">
		<div class="col-md-12" id="page-content">
		
		<c:if test="${not empty error_msg}">
			<div class="container col-lg-12" id="transferSuccess">
				<div class="alert alert-dismissible alert-success col-lg-6">
					<button type="button" class="close" data-dismiss="alert">&times;</button>
					<strong>${error_msg}</strong>
				</div>
			</div>
		</c:if>
			<h4>Transactions</h4>
			
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title">Filters</h3>
				</div>
				<div class="panel-body">
					<div class="col-lg-7">
						<form class="form-horizontal" action="transactionsearch" method='POST' onSubmit="return checkInputOr()">
							<label for="requestID" class="col-lg-3 control-label">Transaction ID : </label>
							<div class="col-lg-9">
		       					<input type="text" class="form-control" name="transactionID" placeholder="Transaction ID">
		      				</div>
		      				<br><br>
		      				<input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
						    <button type="submit" class="btn btn-primary">Submit</button>
						    <a href="transaction" class="btn btn-primary">All Transaction</a>
						</form>
					</div>
				</div>
			</div>
			
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title">Pending Transaction Requests</h3>
				</div>
				<div class="panel-body no-padding">
					<table id="content-table">
						<thead>
							<tr>
								<th class="active">Transaction ID</th>
								<th class="active">Sender</th>
								<th class="active">Receiver</th>
								<th class="active">Amount</th>
								<th class="active">Type</th>
								<th class="active">Critical</th>
								<th class="active">Action</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
                        		<c:when test="${empty pending_list}">
                        			<tr>
                                    	<td colspan="7">No Pending Transaction</td>
                                	</tr>
                        		</c:when>
                        		<c:otherwise>
                        			<c:forEach items="${pending_list}" var="transaction">
                        				<c:if test="${transaction.critical}">
                        					<tr>
                                    		<td style="text-align:center">${transaction.id}</td>
											<td style="text-align:center">${transaction.payer_id}</td>
											<td style="text-align:center">${transaction.payee_id}</td>
											<td style="text-align:center">${transaction.amount}</td>
											<td style="text-align:center">${transaction.transaction_type}</td>
											<td style="text-align:center">${transaction.critical}</td>
											<td style="text-align:center">
												<form action = "processtransaction" method = "post">
		                                    		<input type="hidden" name="transactionID" value="${transaction.id}">
		                                    		<input type="hidden" name="extUserID" value="external">
		                                    		<input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
		                                    		<select id="requestType" name="requestType" required>
				       									<option value="">Select Type</option>
				          								<option value="approve">Approve</option>
				          								<option value="reject">Reject</option>
				       								</select>
		                                    		<button type="submit" class="btn btn-xs btn-primary">Submit</button>
		                                   		</form>
											</td>
                                		</tr>
                        				</c:if>
                                		<c:if test="${!transaction.critical}">
												<form action = "processtransaction" method = "post">
	                        						<tr>
			                                    		<td style="text-align:center">${transaction.id}</td>
														<td style="text-align:center">${transaction.payer_id}</td>
														<td style="text-align:center"><input type="text" name="amount" value="${transaction.payee_id}"></td>
														<td style="text-align:center"><input type="text" name="amount" value="${transaction.amount}"></td>
														<td style="text-align:center">${transaction.transaction_type}</td>
														<td style="text-align:center">${transaction.critical}</td>
														<td style="text-align:center">
					                                    		<input type="hidden" name="transactionID" value="${transaction.id}">
					                                    		<input type="hidden" name="extUserID" value="external">
					                                    		<input type="hidden"  name="${_csrf.parameterName}"   value="${_csrf.token}"/>
					                                    		<select id="requestType" name="requestType" required>
							       									<option value="">Select Type</option>
							          								<option value="approve">Approve</option>
							          								<option value="reject">Reject</option>
							       								</select>
					                                    		<button type="submit" class="btn btn-xs btn-primary">Submit</button>
														</td>
		                                			</tr>
                        						</form>
                        				</c:if>
                            		</c:forEach>
                        		</c:otherwise>
                        	</c:choose>
						</tbody>
					</table>
				</div>
			</div>
			<div class="panel panel-primary">
				<div class="panel-heading">
					<h3 class="panel-title">Complete Transactions Requests</h3>
				</div>
				<div class="panel-body no-padding">
					<table id="content-table">
						<thead>
							<tr>
								<th class="active">ID</th>
								<th class="active">Sender Account</th>
								<th class="active">Receiver Account</th>
								<th class="active">Amount</th>
								<th class="active">Type</th>
								<th class="active">Critical</th>
								<th class="active">Status</th>
								<th class="active">Approver</th>
							</tr>
						</thead>
						<tbody>
							<c:choose>
                        		<c:when test="${empty complete_list}">
                        			<tr>
                                    	<td colspan="8">No Completed Transaction</td>
                                	</tr>
                        		</c:when>
                        		<c:otherwise>
                        			<c:forEach items="${complete_list}" var="transaction">
										<tr>
											<td style="text-align:center">${transaction.id}</td>
											<td style="text-align:center">${transaction.payer_id}</td>
											<td style="text-align:center">${transaction.payee_id}</td>
											<td style="text-align:center">${transaction.amount}</td>
											<td style="text-align:center">${transaction.transaction_type}</td>
											<td style="text-align:center">${transaction.critical}</td>
											<td style="text-align:center">${transaction.status}</td>
											<td style="text-align:center">${transaction.approver}</td>
										</tr>
									</c:forEach>
                        		</c:otherwise>
                        	</c:choose>	
						</tbody>
					</table>
				</div>
			</div>
			
		</div>
	</div> <!-- .content-wrapper -->
	
</main> 

<script type="text/javascript">
	$(document).ready(function() {
		sideNavigationSettings();
	});
	</script>

</body>
</html>