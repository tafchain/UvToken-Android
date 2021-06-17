package com.yongqi.wallet.utils;

import android.app.Activity;
import android.util.Log;

import api.AddCoinTypeRequest;
import api.AddCoinTypeResponse;
import api.AecoSignRequest;
import api.AecoSignRequestFromMaster;
import api.AecoSignResponse;
import api.AecoSignResponseFromMaster;
import api.BackupMnemonicRequest;
import api.BackupMnemonicResponse;
import api.CreateWalletRequest;
import api.CreateWalletResponse;
import api.ETHEipAddressRequest;
import api.ETHEipAddressResponse;
import api.ETHTransactionRequest;
import api.ETHTransactionResponse;
import api.EstimateEthGasPriceRequest;
import api.EstimateEthGasPriceResponse;
import api.EstimateTransactionSizeRequest;
import api.EstimateTransactionSizeResponse;
import api.ExportPrivateKeyFromMasterRequest;
import api.ExportPrivateKeyFromMasterResponse;
import api.ExportPrivateKeyRequest;
import api.ExportPrivateKeyResponse;
import api.GetAddressBalanceRequest;
import api.GetAddressBalanceResponse;
import api.GetAddressesTxIdsRequest;
import api.GetAddressesTxIdsResponse;
import api.GetBtcTransactionRequest;
import api.GetBtcTransactionResponse;
import api.ImportPrivateKeyRequest;
import api.ImportPrivateKeyResponse;
import api.ImportWalletFromMnemonicRequest;
import api.ImportWalletFromMnemonicResponse;
import api.ModifyPasswordRequest;
import api.ModifyPasswordResponse;
import api.ParamRequest;
import api.ParamResponse;
import api.QueryETHTransactionRequest;
import api.QueryETHTransactionResponse;
import api.QueryTrxTransactionRequest;
import api.QueryTrxTransactionResponse;
import api.RemoveWalletRequest;
import api.RemoveWalletResponse;
import api.ToTrxAddressRequest;
import api.ToTrxAddressResponse;
import api.TransactionFeeRateRequest;
import api.TransactionFeeRateResponse;
import api.TransferFromHdWalletRequest;
import api.TransferFromHdWalletResponse;
import api.TransferRequest;
import api.TransferResponse;
import api.ValidateAddressRequest;
import api.ValidateAddressResponse;
import api.VerifyMultiMnemonicRequest;
import api.VerifyMultiMnemonicResponse;
import api.VerifyWalletPasswordRequest;
import api.VerifyWalletPasswordResponse;
import uv1.Uv1;

public class Uv1Helper {
    public interface ResponseDataCallback<T>  {
        void onSuccess(T data);
        void onError(Exception e);
    }

    //初始化配置
    public static void paramConfig(Activity activity,ParamRequest request, ResponseDataCallback<ParamResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ParamResponse response = Uv1.paramConfig(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","paramConfig:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //修改密码
    public static void modifyPassword(Activity activity, ModifyPasswordRequest request, ResponseDataCallback<ModifyPasswordResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ModifyPasswordResponse response = Uv1.modifyPassword(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","modifyPassword:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //创建钱包
    public static void createWallet(Activity activity,CreateWalletRequest request, ResponseDataCallback<CreateWalletResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                CreateWalletResponse response = Uv1.createWallet(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","createWallet:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //获取地址余额
    public static void getAddressBalance(Activity activity,GetAddressBalanceRequest request, ResponseDataCallback<GetAddressBalanceResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                GetAddressBalanceResponse response = Uv1.getAddressBalance(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","getAddressBalance:" + request.getAddress() + ":" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //导出多链钱包
    public static void exportPrivateKeyFromMaster(Activity activity,ExportPrivateKeyFromMasterRequest request, ResponseDataCallback<ExportPrivateKeyFromMasterResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ExportPrivateKeyFromMasterResponse response = Uv1.exportPrivateKeyFromMaster(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","exportPrivateKeyFromMaster:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //导出单链钱包
    public static void exportPrivateKey(Activity activity,ExportPrivateKeyRequest request, ResponseDataCallback<ExportPrivateKeyResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ExportPrivateKeyResponse response = Uv1.exportPrivateKey(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","exportPrivateKey:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }


    //校验密码
    public static void verifyWalletPassword(Activity activity,VerifyWalletPasswordRequest request, ResponseDataCallback<VerifyWalletPasswordResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                VerifyWalletPasswordResponse response = Uv1.verifyWalletPassword(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","verifyWalletPassword:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //备份助记词
    public static void backupMnemonic(Activity activity,BackupMnemonicRequest request, ResponseDataCallback<BackupMnemonicResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                BackupMnemonicResponse response = Uv1.backupMnemonic(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","backupMnemonic:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //助记词导入钱包
    public static void verifyMultiMnemonic(Activity activity,VerifyMultiMnemonicRequest request, ResponseDataCallback<VerifyMultiMnemonicResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                VerifyMultiMnemonicResponse response = Uv1.verifyMultiMnemonic(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","verifyMultiMnemonic:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //导入私钥
    public static void importPrivateKey(Activity activity,ImportPrivateKeyRequest request,ResponseDataCallback<ImportPrivateKeyResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ImportPrivateKeyResponse response = Uv1.importPrivateKey(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","importPrivateKey:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //添加币种
    public static void addCoinType(Activity activity,AddCoinTypeRequest addCoinTypeRequest,ResponseDataCallback<AddCoinTypeResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                AddCoinTypeResponse response = Uv1.addCoinType(addCoinTypeRequest);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","addCoinType:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //助记词导入钱包
    public static void importWalletFromMnemonic(Activity activity,ImportWalletFromMnemonicRequest request, ResponseDataCallback<ImportWalletFromMnemonicResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ImportWalletFromMnemonicResponse response = Uv1.importWalletFromMnemonic(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","importWalletFromMnemonic:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //删除多链钱包
    public static void removeMultiWallet(Activity activity,RemoveWalletRequest request, ResponseDataCallback<RemoveWalletResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                RemoveWalletResponse response = Uv1.removeWallet(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","removeMultiWallet:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //查询交易
    public static void getAddressesTxIds(Activity activity,GetAddressesTxIdsRequest request, ResponseDataCallback<GetAddressesTxIdsResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                GetAddressesTxIdsResponse response = Uv1.getAddressesTxIds(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","getAddressesTxIds:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //ETH转账获取费率
    public static void estimateEthGasPrice(Activity activity,EstimateEthGasPriceRequest request, ResponseDataCallback<EstimateEthGasPriceResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                EstimateEthGasPriceResponse response = Uv1.estimateEthGasPrice(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","estimateEthGasPrice:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //BTC转账获取费率
    public static void transactionFeeRate(Activity activity,TransactionFeeRateRequest request, ResponseDataCallback<TransactionFeeRateResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                TransactionFeeRateResponse response = Uv1.transactionFeeRate(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","transactionFeeRate:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //BTC交易获取到size
    public static void estimateTransactionSize(Activity activity,EstimateTransactionSizeRequest request, ResponseDataCallback<EstimateTransactionSizeResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                EstimateTransactionSizeResponse response = Uv1.estimateTransactionSize(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","estimateTransactionSize:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //校验输入的地址是否合法
    public static void validateAddress(Activity activity,ValidateAddressRequest request, ResponseDataCallback<ValidateAddressResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ValidateAddressResponse response = Uv1.validateAddress(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","validateAddress:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //多链钱包交易
    public static void transferFromHdWallet(Activity activity,TransferFromHdWalletRequest request, ResponseDataCallback<TransferFromHdWalletResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                TransferFromHdWalletResponse response = Uv1.transferFromHdWallet(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","transferFromHdWallet:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //单链钱包交易
    public static void transferFromKey(Activity activity,TransferRequest request, ResponseDataCallback<TransferResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                TransferResponse response = Uv1.transferFromKey(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","transferFromKey:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //获取btc交易
    public static void getBtcTransaction(Activity activity, GetBtcTransactionRequest request, ResponseDataCallback<GetBtcTransactionResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                GetBtcTransactionResponse response = Uv1.getBtcTransaction(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","getBtcTransaction:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }


    //unaswap单链钱包签名
    public static void sign(Activity activity, AecoSignRequest request, ResponseDataCallback<AecoSignResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                AecoSignResponse response = Uv1.sign(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","sign:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }


    //unaswap多链链钱包签名
    public static void signFromMaster(Activity activity, AecoSignRequestFromMaster request, ResponseDataCallback<AecoSignResponseFromMaster> callback) {
        Thread thread = new Thread(() -> {
            try {
                AecoSignResponseFromMaster response = Uv1.signFromMaster(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","signFromMaster:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //发送ETH交易
    public static void sendETHTransaction(Activity activity, ETHTransactionRequest request, ResponseDataCallback<ETHTransactionResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ETHTransactionResponse response = Uv1.sendETHTransaction(request,false);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","sendETHTransaction:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //地址转换
    public static void toEip55Address(Activity activity, ETHEipAddressRequest request, ResponseDataCallback<ETHEipAddressResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                ETHEipAddressResponse response = Uv1.toEip55Address(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","sendETHTransaction:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }



    //查询eth交易订单状态
    public static void getEthTransactionReceipt(Activity activity, QueryETHTransactionRequest request, ResponseDataCallback<QueryETHTransactionResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                QueryETHTransactionResponse response = Uv1.getETHTransactionReceipt(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","getETHTransactionReceipt:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }

    //trx地址转换
    public static void toTrxAddress(Activity activity, ToTrxAddressRequest request, ResponseDataCallback<ToTrxAddressResponse> callback) {
//        Thread thread = new Thread(() -> {
        Log.e("toTrxAddress","start=" + System.currentTimeMillis() + "");
            try {
                ToTrxAddressResponse response = Uv1.toTrxAddress(request);
                Log.e("toTrxAddress","end=" + System.currentTimeMillis() + "");
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","toTrxAddress:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
//        });
//        thread.start();
    }

    //trx查询交易详情
    public static void getTrxTransaction(Activity activity, QueryTrxTransactionRequest request, ResponseDataCallback<QueryTrxTransactionResponse> callback) {
        Thread thread = new Thread(() -> {
            try {
                QueryTrxTransactionResponse response = Uv1.getTrxTransaction(request);
                activity.runOnUiThread(() -> callback.onSuccess(response));
            }catch (Exception e) {
                Log.e("sdk_error_info","getTrxTransaction:" + e.getLocalizedMessage());
                activity.runOnUiThread(() -> callback.onError(e));
            }
        });
        thread.start();
    }



}
