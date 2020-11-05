/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 * <p/>
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 * <p/>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.constants;

import lombok.NoArgsConstructor;

/**
 * Constants values.
 * Created by tarkeshwar.v on 2/8/2017.
 */

public class ServerConfig {
    private ServerConfig(){}
    public static final String MDES_IP = "http://localhost";
    public static final String MDES_PORT = "3000";
    public static final String DIGITIZATION_PATH = "/mdes/digitization/1/0";

    public static final String CMSD_IP = "http://localhost";
    public static final String CMSD_PORT = "7004";
    public static final String TDS_PATH = "/mdes/tds/1/0";
    public static final String RESPONSE_HOST = "com.mahindracomviva.payappserver";
}
