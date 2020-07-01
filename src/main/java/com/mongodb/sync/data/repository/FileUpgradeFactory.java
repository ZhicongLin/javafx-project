package com.mongodb.sync.data.repository;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.FormBodyPartBuilder;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSON;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.sync.data.vo.ResultMessage;

import lombok.extern.slf4j.Slf4j;

/**
 * Description:
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2020/6/17.1    linzc       2020/6/17     Create
 * </pre>
 * @date 2020/6/17
 */
@Slf4j
public class FileUpgradeFactory {

    /**
     * 上传到新的服务器
     * @param url
     * @param dbFile
     * @param id
     * @return MD5
     */
    public static String upgrade(String url, GridFSDBFile dbFile, String id) {
        try {
            final Map<String, Object> param = new HashMap<>();
            param.put("fileId", id);
            param.put("fileName", dbFile.getFilename());
            param.put("file", dbFile.getInputStream());
            final String json = postFile(url, param, dbFile.getFilename());
            final ResultMessage resultMessage = JSON.parseObject(json, ResultMessage.class);
            if (resultMessage.isSuccess()) {
                return resultMessage.getData().toString();
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * post上传文件
     * @param url
     * @param param
     * @param fileName
     * @return
     * @throws IOException
     */
    private static String postFile(String url, Map<String, Object> param, String fileName) throws IOException {
        String res;
        final CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.setEntity(createEntity(param, fileName));
        final CloseableHttpResponse response = httpClient.execute(httppost);
        final HttpEntity entity = response.getEntity();
        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == HttpStatus.SC_OK) {
            res = EntityUtils.toString(entity, "UTF-8");
            response.close();
        } else {
            res = EntityUtils.toString(entity, "UTF-8");
            response.close();
            throw new IllegalArgumentException(statusCode + " " + res);
        }
        return res;
    }

    private static HttpEntity createEntity(Map<String, Object> param, String fileName) {
        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        param.forEach((key, value) -> {
            try {
                builder.addPart(createBodyPart(key, value, fileName));
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage(), e);
            }
        });
        return builder.build();
    }

    private static FormBodyPart createBodyPart(String key, Object value, String fileName) throws UnsupportedEncodingException {
        ContentBody body;
        if (value instanceof InputStream) {
            body = new InputStreamBody((InputStream) value, fileName);
        } else {
            body = new StringBody(value.toString());
        }
        final FormBodyPartBuilder fileBuilder = FormBodyPartBuilder.create(key, body);
        return fileBuilder.build();
    }

}