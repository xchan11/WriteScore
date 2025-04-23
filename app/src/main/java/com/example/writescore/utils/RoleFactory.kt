package tuorong.com.healthy.utils

import com.google.gson.internal.LinkedTreeMap
import tuorong.com.healthy.model.LoginInfo

object RoleFactory {
    fun createRole(obj: LinkedTreeMap<*, *>): LoginInfo.UserInfo {
        return LoginInfo.UserInfo(
            id = obj["id"] as? String ?: "", // 用户ID、专家ID、客服ID
            age = (obj["age"] as? Number)?.toInt() ?: 0, // 用户年龄、专家年龄
            sex = (obj["sex"] as? Number)?.toInt() ?: 0, // 用户性别、专家性别、客服性别 (0: 男, 1: 女)
            phone = obj["phone"] as? String ?: "", // 用户电话、专家电话
            area = obj["area"] as? ArrayList<String> ?: arrayListOf(), // 用户地区、专家地区
            email = obj["email"] as? String ?: "", // 用户邮箱、专家邮箱
            avatar = obj["avatar"] as? String ?: "", // 用户头像、专家头像、客服头像
            invitationCode = obj["invitationCode"] as? String ?: "", // 用户邀请码、客服邀请码
            createTime = obj["createTime"] as? String ?: "", // 专家创建时间、客服创建时间
            updateTime = obj["updateTime"] as? String ?: "", // 专家更新时间、客服更新时间
            isDelete = (obj["isDelete"] as? Number)?.toInt() ?: 0, // 专家是否删除、客服是否删除(0: 否, 1: 是)
            name = obj["name"] as? String ?: "", // 客服姓名、专家姓名

            // 用户特有字段
            nickName = obj["nickName"] as? String ?: "", // 用户昵称
            trueName = obj["trueName"] as? String ?: "", // 用户真实姓名
            birthday = obj["birthday"] as? String ?: "", // 用户生日
            expertId = obj["expertId"] as? String ?: "", // 用户对接专家ID
            expertName = obj["expertName"] as? String ?: "", // 用户对接专家ID
            educational = if((obj["educational"] as? String) == "") "" else (obj["educational"] as? String ?: ""), // 用户学历
            job = if((obj["job"] as? String) == "") "" else (obj["job"] as? String ?: ""), // 用户职位
            agentId = obj["agentId"] as? String ?: "", // 用户代理ID
            profile = obj["profile"] as? String ?: "", // 用户个人简介
            bindingProvider = obj["bindingProvider"] as? Boolean ?: false,


            // 专家特有字段
            degree = (obj["degree"] as? Number)?.toInt() ?: 0, // 专家学位、客服等级
            level = (obj["level"] as? Number)?.toInt() ?: 0, // 专家级别
            position = obj["position"] as? String ?: "", // 专家职位
            professional = obj["professional"] as? String ?: "", // 专家专业
            status = (obj["status"] as? Number)?.toInt() ?: 0, // 专家状态
            workTime = obj["workTime"] as? String ?: "", // 专家工作时间
            workplace = obj["workplace"] as? String ?: "", // 专家工作地点

            // 客服特有字段
            providerId = obj["providerId"] as? String ?: "" // 客服提供者ID
        )
    }
}

