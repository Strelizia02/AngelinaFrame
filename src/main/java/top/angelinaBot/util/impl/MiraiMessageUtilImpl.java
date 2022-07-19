package top.angelinaBot.util.impl;

import lombok.extern.slf4j.Slf4j;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.*;
import net.mamoe.mirai.message.data.*;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.css.CSS2Properties;
import top.angelinaBot.dao.ActivityMapper;
import top.angelinaBot.model.ReplayInfo;
import top.angelinaBot.util.SendMessageUtil;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Mirai发送消息封装方法
 */
@Component("mirai")
@Slf4j
public class MiraiMessageUtilImpl implements SendMessageUtil {

    @Autowired
    private ActivityMapper activityMapper;



    /**
     * 发送群消息方法
     * @param replayInfo 发送消息的结构封装
     */
    @Override
    public void sendGroupMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        try{
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();
        List<ExternalResource> replayAudioList = replayInfo.getReplayAudio();
        String kick = replayInfo.getKick();
        Long AT = replayInfo.getAT();
        Integer muted = replayInfo.getMuted();
        Long nudged = replayInfo.getNudged();
        Boolean isMutedAll = replayInfo.getMutedAll();
        Boolean permission = replayInfo.getPermission();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取群对象
        Group group = bot.getGroupOrFail(replayInfo.getGroupId());

        //@，文字和图像任意出现则创建消息链
        if(replayMessage != null||AT != null||replayImgList.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在@内容，加入@内容
            if (AT != null){
                messageChainBuilder.append(new At(AT));
            }
            //当存在文字内容，加入文字内容
            if (replayMessage != null){
                if (replayMessage.contains("杰哥口我")||replayMessage.contains("洁哥口我")||replayMessage.contains("安洁莉娜口我")){
                    replayMessage = "达咩";
                }
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0){
                for (ExternalResource replayImg: replayImgList){
                    messageChainBuilder.append(group.uploadImage(replayImg));
                    replayImg.close();
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            group.sendMessage(chain);
        }

        if (kick != null) {
            //踢出群
            group.getOrFail(replayInfo.getQq()).kick("");
        }

        if (muted != null) {
            //禁言muted分钟
            group.getOrFail(replayInfo.getQq()).mute(muted);
        }

        if (nudged != null) {
            //获取戳一戳的群对象
            Member member = group.getOrFail(nudged);
            //戳一戳
            member.nudge().sendTo(group);
        }

        if( isMutedAll ) {
            //全体禁言功能开启
             group.getSettings().setMuteAll(true);

        }

        if (replayAudioList.size() > 0){
            //发送语音
            for (ExternalResource replayAudio: replayAudioList){
                group.sendMessage(group.uploadAudio(replayAudio));
                replayAudio.close();
            }
        }

        if(permission){
            MemberPermission memberPermission = group.getBotPermission();
            group.sendMessage(String.valueOf(memberPermission.getLevel()));
        }

        log.info("发送消息" + replayInfo.getReplayMessage());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("发送消息失败");
        }
    }

    @Override
    public void sendFriendMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取用户对象
        User user = bot.getFriendOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0) {
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
                    messageChainBuilder.append(user.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            user.sendMessage(chain);
        }
        log.info("发送朋友私聊消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendStrangerMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取陌生人对象
        Stranger stranger = bot.getStrangerOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
                    messageChainBuilder.append(stranger.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            stranger.sendMessage(chain);
        }
        log.info("发送陌生人私聊消息" + replayInfo.getReplayMessage());
    }

    @Override
    public void sendGroupTempMsg(ReplayInfo replayInfo) {
        activityMapper.sendMessage();
        //解析replayInfo
        String replayMessage = replayInfo.getReplayMessage();
        List<ExternalResource> replayImgList = replayInfo.getReplayImg();

        //获取登录bot
        Bot bot = Bot.getInstance(replayInfo.getLoginQQ());
        //获取群对象
        Group group = bot.getGroupOrFail(replayInfo.getGroupId());
        //获取成员对象
        Member member = group.getOrFail(replayInfo.getQq());

        //文字和图像任意出现则创建消息链
        if (replayMessage != null || replayImgList.size() > 0){
            MessageChainBuilder messageChainBuilder = new MessageChainBuilder();
            //当存在文字内容，加入文字内容
            if (replayMessage != null) {
                messageChainBuilder.append(new PlainText(replayMessage));
            }
            //当存在图片，加入图片的内容
            if (replayImgList.size() > 0) {
                for (ExternalResource replayImg : replayImgList) {
                    messageChainBuilder.append(member.uploadImage(replayImg));
                }
            }
            //构建消息链并发送
            MessageChain chain = messageChainBuilder.build();
            member.sendMessage(chain);
        }
        log.info("发送临时会话私聊消息" + replayInfo.getReplayMessage());
    }
}
