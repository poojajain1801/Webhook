/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.ApproveHvtRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SetHvtValueRequest;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 01-04-2019.
 */
public interface ConfigurationService {
    /**
     * setHvtValue
     * @param setHvtValueRequestpojo setHvtValueReuPojo
     * @return map
     * */
    Map<String,Object> setHvtValue(SetHvtValueRequest setHvtValueRequestpojo);

    /**
     * getPendingRequests
     * @return map
     * */
    Map<String,Object> getPendingRequests();

    /**
     * approveHvt
     * @param approveHvtRequest aprroveHvtRequest
     * @return map
     * */
    Map<String,Object> approveHvt(ApproveHvtRequest approveHvtRequest);
}
