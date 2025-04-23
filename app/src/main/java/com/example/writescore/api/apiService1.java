package com.example.writescore.api;

import androidx.lifecycle.LiveData;

import java.util.List;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import tuorong.com.healthy.model.Address;
import tuorong.com.healthy.model.AppUpdate;
import tuorong.com.healthy.model.Consumer4Service;
import tuorong.com.healthy.model.ConsumerData;
import tuorong.com.healthy.model.ConsumerDetail;
import tuorong.com.healthy.model.ConsumerFeedback;
import tuorong.com.healthy.model.ContactInfo;
import tuorong.com.healthy.model.CurrentIntegral;
import tuorong.com.healthy.model.DrawHistory;
import tuorong.com.healthy.model.EvaluationItem;
import tuorong.com.healthy.model.ExpertFeedbackItem;
import tuorong.com.healthy.model.ExpertInfoBean;
import tuorong.com.healthy.model.FavoriteDetail;
import tuorong.com.healthy.model.FavoriteList;
import tuorong.com.healthy.model.FeedBack;
import tuorong.com.healthy.model.FeedBackDetail;
import tuorong.com.healthy.model.FeedBackTemplate;
import tuorong.com.healthy.model.FeedbackItem;
import tuorong.com.healthy.model.IntegralRule;
import tuorong.com.healthy.model.InviteExperienceData;
import tuorong.com.healthy.model.LoginInfo;
import tuorong.com.healthy.model.MarkReadRequest;
import tuorong.com.healthy.model.Medias;
import tuorong.com.healthy.model.MedicalReport;
import tuorong.com.healthy.model.Problem;
import tuorong.com.healthy.model.Product;
import tuorong.com.healthy.model.ProductDetail;
import tuorong.com.healthy.model.Provider;
import tuorong.com.healthy.model.ReportData;
import tuorong.com.healthy.model.RequestType;
import tuorong.com.healthy.model.ShoppingCart;
import tuorong.com.healthy.model.SystemMessage;
import tuorong.com.healthy.model.Template;
import tuorong.com.healthy.model.UserId;
import tuorong.com.healthy.model.WorkLoadDataDetail;
import tuorong.com.healthy.model.local.DetailReport;
import tuorong.com.healthy.model.local.FileBean;
import tuorong.com.healthy.model.local.QuestionData;
import tuorong.com.healthy.model.local.ServicePageInfo;
import tuorong.com.healthy.model.message.ContactUser;
import tuorong.com.healthy.model.message.IMMessage;
import tuorong.com.healthy.model.message.RecentContact;
import tuorong.com.healthy.model.pager.Pager;
import tuorong.com.healthy.model.payment.DetailOrders;
import tuorong.com.healthy.model.payment.Logistics;
import tuorong.com.healthy.model.payment.OrdersSource;
import tuorong.com.healthy.model.payment.PayOrder;
import tuorong.com.healthy.model.service.ExpertAdviceBean;
import tuorong.com.healthy.model.service.ExpertInfoData;
import tuorong.com.healthy.model.service.ProductsPlanHistory;
import tuorong.com.healthy.model.service.ServiceInfoBean;
import tuorong.com.healthy.model.service.ServiceInviteData;
import tuorong.com.healthy.model.service.ServiceWorkData;

/**
 * api接口统一管理
 */
public interface apiService1 {
    /**
     * **************************公共接口**********************************************************
     * */
    //统一登录接口(√√√)
    @POST("user/login")
    LiveData<RequestType<LoginInfo>> loginByPhoneAndPwd(@Body RequestBody body);

    //统一登出接口(√√√)
    @POST("user/logout")
    LiveData<RequestType> logout();

    //统一登出接口(√√√)
    @POST("user/logout")
    Call<RequestType> logout2();

    // 发送登录验证码(√√√)
    @POST("user/login/send-sms")
    LiveData<RequestType> getLoginVerifiedCode(@Body RequestBody body);

    //短信验证码登录(√√√)
    @POST("user/login/sms")
    LiveData<RequestType<LoginInfo>> loginByPhoneAndSms(@Body RequestBody body);

    // 获取上传所需要的Token(√√√)
    @GET("common/get-store-crt")
    LiveData<RequestType> getUpToken();
    @GET("common/get-store-crt")
    Call<RequestType<String>> getUpToken2();

    // 检查更新
    @GET("user/app//get-latest-edition")
    Call<RequestType<AppUpdate>> getLatestVersion();

    //切服密码下发(√√√)
    @GET("common/get-change-service")
    Call<ResponseBody> fetchPassword();

    @GET("user/chat/get-info-by-userId/{userId}")
    Call<RequestType<ContactInfo>> getContactInfo(@Path("userId")  String userId);

    /*******************************************************************************************
    *********************************   聊天相关   ***********************************************
    *******************************************************************************************/
    @GET("user/chat/get-contact-list")
    LiveData<RequestType<List<ContactUser>>> getContactList();

    @GET("user/chat/get-all-chat-session")
    LiveData<RequestType<List<RecentContact>>> getRecentChatList();

    @POST("user/chat/send-private-message")
    Call<RequestType<String>> sendMessage(@Body IMMessage body);

    /**
     * 根据id标记信息已读
     * */
    @POST("user/chat/mark-message-list-read")
    LiveData<RequestType> markMsgRead(@Body RequestBody body);

    /**
     * 根据id获取通讯信息
     * */
    @GET("user/chat/get-contact-info/{userId}")
    Call<RequestType<ContactUser>> getUserInfoById(@Path("userId") String userId);

    /**
     * 拉取聊天记录
     * */
    @GET("user/chat/get-history-chat-message/{fromUserId}/{beforeMessageID}")
    LiveData<RequestType<List<IMMessage>>> getChatHistory(@Path("fromUserId") String userId,
                                                          @Path("beforeMessageID") String lastMsgId);

    //撤回消息
    @POST("user/chat/withdraw-message")
    Call<RequestType<Object>> revokeMessage(@Body RequestBody body);

    //标记已读，针对联系人
    @POST("user/chat/mark-contact-read")
    Call<RequestType<Object>> markReadAlready(@Body RequestBody body);//contactUserId
    
    //标记已读，仅针对广告 系统消息
    @POST("user/chat/mark-push-read")
    Call<RequestType<Object>> markReadAlready2(@Body RequestBody body);//pushMessageType

    //获取推送、广告消息记录
    @GET("user/chat/get-history-push-message/{beforeMessageID}/{pushMessageType}")
    LiveData<RequestType<List<IMMessage>>> requestPushMessage(@Path("beforeMessageID") int anchor,
                                                 @Path("pushMessageType") String pushType);
    @GET("user/chat/get-history-push-message/{beforeMessageID}/{pushMessageType}")
    LiveData<RequestType<List<SystemMessage>>> requestPushmessage(@Path("beforeMessageID") int anchor,
                                                              @Path("pushMessageType") String pushType);
    @POST("user/chat/mark-push-read")
    LiveData<RequestType<Boolean>> markAllMessagesAsRead(@Body MarkReadRequest markReadRequest);
    @POST("consumer/evaluate/insert-evaluate")
    Call<RequestType<String>> submitScore(@Body RequestBody requestBody);
    //type:0客服 1专家 2产品 3咨询 4建议 5就医转介

    //根据id搜索推文
    @GET("admin/tweet/get-tweet-content/{id}")
    LiveData<RequestType<UserId>> getTweetContentById(@Path("id") String id);


    /**
     * **************************用户端接口*********************************************************
     * */
    //获取用户的详细信息(√√√)
    @GET("consumer/get/login")
    LiveData<RequestType> getUserDetail();

    // 发送注册验证码(√√√)
    @POST("consumer/register/send-sms")
    LiveData<RequestType> getRegisterVerifiedCode(@Body RequestBody body);

    //用户注册接口(√√√) age，sex，avatar,trueName全部给null，nickName给随机生成值,邀请码是可选项,其他是必须项
    @POST("consumer/register")
    LiveData<RequestType> userRegister(@Body RequestBody body);

    //更新用户的信息(√√√)
    @PUT("consumer/update-info")
    LiveData<RequestType> updateUserInfo(@Body RequestBody body);

    //更改用户密码(√√√)
    @POST("consumer/update-password")
    LiveData<RequestType> updatePassword(@Body RequestBody body);

    //发送修改密码短信验证码(√√√)
    @POST("consumer/send-update-password-sms")
    LiveData<RequestType> getUpdatePwdSms(@Body RequestBody body);

    //根据用户等级为客户列出所有商品(√√√)
    @POST("consumer/product/list-product")
    Call<RequestType<Pager<Product>>> getAllProductByLevel(@Body RequestBody body);

    //获取具体的产品细节(√√√)
    @GET("consumer/product/get-product/{id}")
    LiveData<RequestType<ProductDetail>> getProductDetail(@Path("id") String productId);

    //客户将产品加入到购物车(√√√)
    @POST("consumer/product/add-product-to-shoppingCart")
    LiveData<RequestType> addItemToCart(@Body RequestBody body);

    //客户将购物车中某一产品减一(√√√)
    @POST("consumer/product/substance-shoppingCart")
    LiveData<RequestType> moveItemFromCart(@Body RequestBody body);

    //客户清空购物车所有商品 (√√√)
    @POST("consumer/product/delete-product-from-shoppingCart")
    LiveData<RequestType> cleanAllCart();

    //批量删除购物车内容
    @POST("consumer/product/delete-shoppingCart-by-ids")
    Call<RequestType> cleanCartInArray(@Body RequestBody body);

    //客户修改商品数量(√√√)
    @POST("consumer/product/modify-product-quantity-in-shoppingCart")
    LiveData<RequestType> modifyCount(@Body RequestBody body);

    //客户将某一产品移除购物车(√√√)
    @POST("consumer/product/delete-one-product-shoppingCart")
    LiveData<RequestType> removeItemFromCart(@Body RequestBody body);

    //获取用户购物车的所有商品(√√√)
    @POST("consumer/product/list-product-from-shoppingCart")
    LiveData<RequestType<ShoppingCart>> displayAllCart();

    //列出体检报告附件类型(√√√)
    @POST("admin/medical-report-type/common/list")
    LiveData<RequestType<MedicalReport>> getAllExamineType(@Body RequestBody body);

    //客户获取专家聊天时发的健康自测问卷模板(√√√)
    @POST("consumer/health-test/get-expert-health-test-template")
    LiveData<RequestType<QuestionData>> getHealthSelfTestQuestionnaire(@Body RequestBody body);

    //返回客户提交的所有体检报告调查问卷(√√√)
    @POST("consumer/medical-report/list-consumer-medical-report-record")
    LiveData<RequestType<Pager<FileBean>>> getMedicalReports(@Body RequestBody body);

    //客户提交专家聊天时发的健康自测问卷模板(√√√)
    @POST("consumer/health-test/health-test-record")
    LiveData<RequestType> submitHealthSelfTestQuestionnaire(@Body RequestBody body);

    //客户提交专家聊天时发的健康自测问卷模板(√√√)
    @POST("consumer/health-test/list-health-test-record")
    LiveData<RequestType<Pager<FileBean>>> getAllHealthSelfTestQuestionnaire(@Body RequestBody body);

    //客户获取聊天时专家发的体检报告调查问卷模板(√√√)
    @POST("consumer/medical-report/get-expert-health-medical-report-template")
    LiveData<RequestType<QuestionData>> getPhysicalExaminationQuestionnaire(@Body RequestBody body);

    //客户提交聊天时专家发的体检报告调查问卷模板(√√√)
    @POST("consumer/medical-report/commit-consumer-medical-report-record")
    LiveData<RequestType> submitPhysicalExaminationQuestionnaire(@Body RequestBody body);

    //获取当前登录客户的等级信息(√√√)
    @GET("consumer/get-consumer-integral")
    Call<RequestType<CurrentIntegral>> getConsumerLevel();

    //用户会员积分等级列表(√√√)
    @POST("admin/integral-rank/list-integral-rank")
    Call<RequestType<Pager<IntegralRule>>> listIntegralRank(@Body RequestBody body);

    //获取当前登录客户的收货地址(√√√)
    @GET("consumer/address/list-consumer-address")
    LiveData<RequestType<List<Address>>> getAddress();

    //删除收货地址(√√√)
    @DELETE("consumer/address/delete-consumer-address/{id}")
    LiveData<RequestType> deleteAddress(@Path("id") Integer id);

    //增加收货地址(√√√)
    @POST("consumer/address/add-consumer-address")
    LiveData<RequestType> addAddress(@Body Address address);

    //修改收货地址(√√√)
    @POST("consumer/address/update-consumer-address")
    LiveData<RequestType> updateAddress(@Body Address address);

    //获取默认地址(√√√)
    @GET("consumer/address/get-default-address")
    LiveData<RequestType<Address>> getDefaultAddress();

    //设置默认地址(√√√)
    @POST("consumer/address//set-default-address/{id}")
    LiveData<RequestType> updateDefaultAddress(@Path("id") Integer id);

    //根据id查询用户体检报告记录(√√√)
    @GET("consumer/medical-report/get-consumer-medical-report-record/{id}")
    LiveData<RequestType<DetailReport>> getMedicalReportById(@Path("id") Integer id);

    //根据id查询用户体检报告记录(√√√)
    @GET("consumer/health-test/get-health-test-record/{id}")
    LiveData<RequestType<DetailReport>> getHyTestById(@Path("id") Integer id);

    //客户对比已提交的体检报告调查问卷(√√√)
    @GET("consumer/medical-report/comparison-consumer-medical-report-record")
    LiveData<RequestType> compareReportById(@Body RequestBody body);

    //客户提交个性化需求分析(√√√)
    @POST("user/feedBack/commit-demand-feedback")
    LiveData<RequestType> submitFeedback(@Body RequestBody body);

    //客户获取提交过的个性化需求分析(√√√)
    @POST("user/feedBack/get-demand-feedback")
    Call<RequestType<Pager<FeedBack>>> getFeedbacks(@Body RequestBody body);

    //获取用户学历
    @GET("user/common/educational-name-list")
    LiveData<RequestType> getUserEducation();

    //客户获取订单列表
    // 未支付0 未发货1 已发货2 送达3 申请退款中4 退款失败5 退款成功6
    @POST("consumer/consumer-order/get-consumer-order-list")
    LiveData<RequestType<List<OrdersSource>>> getOrders(@Body RequestBody body);

    //客户获取订单具体信息
    @GET("consumer/consumer-order/get-consumer-order/{id}")
    LiveData<RequestType<DetailOrders>> getDetailOrder(@Path("id") Integer id);

    //获取客户订单的物流信息
    @GET("consumer/consumer-order/get-order-logistics/{id}")
    LiveData<RequestType<Logistics>> getOrderLogistic(@Path("id") Integer id);

    //判断登录客户是否绑定了区域服务商
    @GET("consumer/check-consumer-bind-provider")
    Call<RequestType> checkBinding();

    //未填写邀请码客户查看所有离客户最近的三级服务商
    @GET("consumer/list-nearby-provider")
    LiveData<RequestType<List<Provider>>> listNearbyProvider();

    //未被邀请的客户选择区域服务商
    @POST("consumer/consumer-binding-provider")
    Call<RequestType> bindingProvider(@Body RequestBody requestBody);

    //未被邀请的客户填写邀请码
    @POST("consumer/consumer-commit-inviter-code/{inviterCode}")
    Call<RequestType> bindingInviteCode(@Path("inviterCode") String inviterCode);

    //获取客户评价记录
    @POST("consumer/evaluate/list-evaluate")
    Call<RequestType<Pager<EvaluationItem>>> getEvaluationRecords(@Body RequestBody requestBody);

    //客户查询专家反馈内容
    @POST("consumer/feedBack/get-expert-feedback")
    Call<RequestType<Pager<ExpertFeedbackItem>>> getExpertFeedback(@Body RequestBody requestBody);

    //客户获取收藏信息组
    @POST("consumer/message-collection/get-message-collection-list")
    Call<RequestType<Pager<FavoriteList>>> getFavorites(@Body RequestBody requestBody);

    //客户获取消息收藏
    @POST("consumer/message-collection/add-message-collection")
    Call<RequestType> getMessageCollection(@Body RequestBody requestBody);

    //客户获取收藏详情
    @POST("consumer/message-collection/get-message-collection-details")
    Call<RequestType<FavoriteDetail>>getMessage(@Body RequestBody requestBody);

    //获取所有的图片和视频聊天记录
    @POST("user/chat/get-history-media")
    LiveData<RequestType<List<Medias>>>getMediasMsg(@Body RequestBody requestBody);

    //下单
    @POST("pay/consumer/make-order")
    LiveData<RequestType<PayOrder>> generateOrder(@Body RequestBody requestBody);

    //付款，目前只支持支付宝
    @POST("pay/consumer/pay-order")
    Call<RequestType<String>> payOrder(@Body PayOrder order);

    //付款，目前只支持微信
    @POST("pay/consumer/pay-order-native")
    Call<RequestType<String>> payOrderByWC(@Body PayOrder order);

    @POST("pay/consumer/get-order-status")
    Call<RequestType<Integer>> checkPayStatus(@Body RequestBody requestBody);

    //订单退款
    @POST("pay/consumer/apply-refund")
    Call<RequestType<DetailOrders>> refundOrder(@Body RequestBody body);

    //退款状态更新
    @POST("pay/consumer/get-refund-status")
    Call<RequestType<Integer>> refundCheckAgain(@Body RequestBody body);

    //确认收货
    @POST("pay/consumer/confirm-receipt")
    Call<RequestType<Object>> confirmReceipt(@Body RequestBody body);
    /**
     * **************************专家端接口*********************************************************
     * */

    @GET("expert/info")
    LiveData<RequestType> getExpertDetail();

    //获取专家的详细信息
    @GET("expert/info")
    LiveData<RequestType<ExpertInfoBean>> getExpertDetailV2();

    //更新专家信息
    @PUT("expert/update-info")
    LiveData<RequestType> updateExpertInfo(@Body LoginInfo.UserInfo expertRole);
    @PUT("expert/update-info")
    Call<RequestType<ExpertInfoBean>> updateExpertInfo2(@Body ExpertInfoBean expertInfoBean);
    //获取专家职称
    @GET("user/common/expert-position-name-list")
    LiveData<RequestType> getExpertPosition();

    //获取专家专业
    @GET("user/common/expert-professional-name-list")
    LiveData<RequestType> getExpertProfessional();

    //获取客户列表
    @POST("expert/get-consumers-basic")
    LiveData<RequestType<ConsumerData>> getConsumerList(@Body RequestBody body);

    //获取客户详细信息
    @POST("expert/get-consumer-info")
    LiveData<RequestType<ConsumerDetail>> getConsumerInfo(@Body RequestBody body);

    //获取初测模板
    @POST("expert/show-health-test-template")
    Call<RequestType<Pager<Template>>> getInitialTrial(@Body RequestBody body);

    //获取体检报告模板
    @POST("expert/show-medical-report-template")
    Call<RequestType<Pager<Template>>> getHealthyTrial(@Body RequestBody body);

    //获取问题标签
    @POST("expert/show-problem-tag")
    LiveData<RequestType<List<Problem>>> getProblems(@Body RequestBody body);

    //获取客户体检报告和相关附件
    @POST("expert/get-medical-report-record-annex")
    LiveData<RequestType<ReportData>> getMedicalReport(@Body RequestBody body);

    //获取专家反馈客户页面的具体信息
    @POST("expert/get-feedback-page-data")
    LiveData<RequestType<FeedBackDetail>> getFeedBackPageData(@Body RequestBody body);

    //获取专家反馈记录
    @POST("expert/get-feedback2consumers")
    Call<RequestType<Pager<FeedbackItem>>> getFeedbackRecord(@Body RequestBody body);

    //获取专家的反馈模板
    @POST("expert/get-feedback-template")
    Call<RequestType<Pager<FeedBackTemplate>>> getFeedbackTemplate(@Body RequestBody body);

    //向客户反馈新增
    @POST("expert/add-feedback2consumer")
    LiveData<RequestType<String>> addFeedBack(@Body RequestBody body);

    //获取对应等级的专家列表
    @POST("expert/consumer-expert")
    Call<RequestType<Pager<ExpertInfoData>>> getExpertList(@Body RequestBody body);

    //转接客户
    @POST("expert/trans-consumer")
    LiveData<RequestType> transferExpert(@Body RequestBody body);

    //获取工作量数据
    @POST("expert/expert-workload-data")
    Call<RequestType<Pager<WorkLoadDataDetail>>> getWorkLoadData(@Body RequestBody body);



    /**
     * **************************客服端接口*********************************************************
     * */
    //获取客服的详细信息(√√√)
    @GET("agent/info")
    LiveData<RequestType> getAgentDetail();

    //获取客服的详细信息(√√√)
    @GET("agent/info")
    LiveData<RequestType<ServiceInfoBean>> getAgentDetailV2();

    //客服商品列表
    @POST("agent/product-list")
    Call<RequestType<Pager<ProductDetail>>> requestProductManageList2(@Body RequestBody body);

    //提交产品库存计划
    @POST("agent/product-plan-modify")
    Call<RequestType<String>> editProductPlan(@Body RequestBody body);

    //提交产品计划
    @POST("agent/add-product-plan")
    Call<RequestType<String>> submitProductPlan(@Body RequestBody body);

    //获取我的反馈记录
    @POST("agent/product-plans")
    Call<RequestType<Pager<ProductsPlanHistory>>> getMyHistoryPlan(@Body ServicePageInfo body);

    //恢复删除的产品计划
    @POST("agent/product-plan-recover")
    Call<RequestType<String>> recoverMyHistoryPlan(@Body RequestBody body);//id：Int

    //删除产品计划
    @POST("agent/delete-product-plan")
    Call<RequestType<String>> deleteMyHistoryPlan(@Body RequestBody body);//id：Int

    //客服获取客户列表
    @POST("agent/get-consumer-list")
    Call<RequestType<Pager<Consumer4Service>>> getConsumerList2(@Body RequestBody body);

    //获取客户可购产品列表
    @POST("agent/consumer-product")
    Call<RequestType<Pager<ProductDetail>>> getConsumerProducts(@Body RequestBody body);

    //获取客户专家列表
    @POST("agent/consumer-expert")
    Call<RequestType<Pager<ExpertInfoData>>> getConsumerExpert(@Body RequestBody body);

    //获取评价列表
    @POST("agent/list-evaluate")
    Call<RequestType<Pager<EvaluationItem>>> getMyEvaluation(@Body RequestBody body);

    /**
     * 客服提现
     * */
    @POST("pay/agent/cash-simulate")
    Call<RequestType<Integer>> withdrawMoney(@Body RequestBody body);//"payType": 0,"amount": 0,"returnType": 0

    //获取提现记录
    @GET("agent/royalty-list")
    Call<RequestType<DrawHistory>> requestWithDrawHistory();

    //获取客服详细等级信息、提成及邀请人数
    @POST("agent/get-service-details")
    Call<RequestType<ServiceWorkData>> requestServiceDetail();

    //客服获取邀请记录
    @POST("agent/invite-record")
    Call<RequestType<Pager<ServiceInviteData>>> getInviteRecord(@Body RequestBody body);

    //客服邀请等级经验
    @POST("agent/invite-experience")
    Call<RequestType<InviteExperienceData>> getInviteExperience();

    //客服获取专家对用户的建议
    @POST("agent/expert2consumer-advise")
    Call<RequestType<List<ExpertAdviceBean>>> getExpert2consumerAdvice(@Body RequestBody body);

    //客服获取客户的反馈
    @POST("agent/consumer-feedback-list")
    Call<RequestType<List<ConsumerFeedback>>> fetchConsumerFeedback(@Body RequestBody body);

    //客服审核客户的反馈
    @PUT("agent/modify-consumer-feedback")
    Call<RequestType<Object>> modifyConsumerFeedback(@Body RequestBody body);

    //客服修改个人信息
    @POST("agent/update-info")
    Call<RequestType<Object>> updateAgentInfo(@Body RequestBody body);

    //客服指派专家
    @POST("agent/assign-expert-for-consumer")
    Call<RequestType<String>> transferExpertByService(@Body RequestBody body);
}
