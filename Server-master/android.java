/*** 查询代币余额 */
public static BigInteger getTokenBalance(Web3j web3j, String fromAddress, String contractAddress) {

    String methodName = "balanceOf";
    List inputParameters = new ArrayList<>();
    List> outputParameters = new ArrayList<>();
    Address address = new Address(fromAddress);
    inputParameters.add(address);

    TypeReference typeReference = new TypeReference() {
    };
    outputParameters.add(typeReference);
    Function function = new Function(methodName, inputParameters, outputParameters);
    String data = FunctionEncoder.encode(function);
    Transaction transaction = Transaction.createEthCallTransaction(fromAddress, contractAddress, data);

    EthCall ethCall;
    BigInteger balanceValue = BigInteger.ZERO;
    try {
        ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).send();
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        balanceValue = (BigInteger) results.get(0).getValue();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return balanceValue;
}

/**
 * 查询代币名称
 *
 * @param web3j
 * @param contractAddress
 * @return
*/
public static String getTokenName(Web3j web3j, String contractAddress) {
    String methodName = "name";
    String name = null;
    String fromAddr = emptyAddress;
    List inputParameters = new ArrayList<>();
    List> outputParameters = new ArrayList<>();

    TypeReference typeReference = new TypeReference() {
    };
    outputParameters.add(typeReference);

    Function function = new Function(methodName, inputParameters, outputParameters);

    String data = FunctionEncoder.encode(function);
    Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

    EthCall ethCall;
    try {
        ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        name = results.get(0).getValue().toString();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    return name;
}

/**
 * 查询代币符号
 *
 * @param web3j
 * @param contractAddress
 * @return
*/
public static String getTokenSymbol(Web3j web3j, String contractAddress) {
    String methodName = "symbol";
    String symbol = null;
    String fromAddr = emptyAddress;
    List inputParameters = new ArrayList<>();
    List> outputParameters = new ArrayList<>();

    TypeReference typeReference = new TypeReference() {
    };
    outputParameters.add(typeReference);

    Function function = new Function(methodName, inputParameters, outputParameters);

    String data = FunctionEncoder.encode(function);
    Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

    EthCall ethCall;
    try {
        ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        symbol = results.get(0).getValue().toString();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    return symbol;
}

/**
 * 查询代币精度
 *
 * @param web3j
 * @param contractAddress
 * @return
*/
public static int getTokenDecimals(Web3j web3j, String contractAddress) {
    String methodName = "decimals";
    String fromAddr = emptyAddress;
    int decimal = 0;
    List inputParameters = new ArrayList<>();
    List> outputParameters = new ArrayList<>();

    TypeReference typeReference = new TypeReference() {
    };
    outputParameters.add(typeReference);

    Function function = new Function(methodName, inputParameters, outputParameters);

    String data = FunctionEncoder.encode(function);
    Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

    EthCall ethCall;
    try {
        ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        decimal = Integer.parseInt(results.get(0).getValue().toString());
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    return decimal;
}

/**
 * 查询代币发行总量
 *
 * @param web3j
 * @param contractAddress
 * @return
*/
public static BigInteger getTokenTotalSupply(Web3j web3j, String contractAddress) {
    String methodName = "totalSupply";
    String fromAddr = emptyAddress;
    BigInteger totalSupply = BigInteger.ZERO;
    List inputParameters = new ArrayList<>();
    List> outputParameters = new ArrayList<>();

    TypeReference typeReference = new TypeReference() {
    };
    outputParameters.add(typeReference);

    Function function = new Function(methodName, inputParameters, outputParameters);

    String data = FunctionEncoder.encode(function);
    Transaction transaction = Transaction.createEthCallTransaction(fromAddr, contractAddress, data);

    EthCall ethCall;
    try {
        ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        List results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
        totalSupply = (BigInteger) results.get(0).getValue();
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    return totalSupply;
}

/**
 * 代币转账
 */
public static String sendTokenTransaction(String fromAddress, String password, String toAddress, String contractAddress, BigInteger amount) {
    String txHash = null;

    try {
        PersonalUnlockAccount personalUnlockAccount = admin.personalUnlockAccount(
            fromAddress, password, BigInteger.valueOf(10)).send();
        if (personalUnlockAccount.accountUnlocked()) {
            String methodName = "transfer";
            List inputParameters = new ArrayList<>();
            List> outputParameters = new ArrayList<>();

            Address tAddress = new Address(toAddress);

            Uint256 value = new Uint256(amount);
            inputParameters.add(tAddress);
            inputParameters.add(value);

            TypeReference typeReference = new TypeReference() {
            };
            outputParameters.add(typeReference);

            Function function = new Function(methodName, inputParameters, outputParameters);

            String data = FunctionEncoder.encode(function);

            EthGetTransactionCount ethGetTransactionCount = web3j.ethGetTransactionCount(fromAddress, DefaultBlockParameterName.PENDING).sendAsync().get();
            BigInteger nonce = ethGetTransactionCount.getTransactionCount();
            BigInteger gasPrice = Convert.toWei(BigDecimal.valueOf(5), Convert.Unit.GWEI).toBigInteger();

            Transaction transaction = Transaction.createFunctionCallTransaction(fromAddress, nonce, gasPrice,
                BigInteger.valueOf(60000), contractAddress, data);

            EthSendTransaction ethSendTransaction = web3j.ethSendTransaction(transaction).sendAsync().get();
            txHash = ethSendTransaction.getTransactionHash();
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    return txHash;
}

/**
 * 计算合约地址
 *
 * @param address
 * @param nonce
 * @return
*/
private static String calculateContractAddress(String address, long nonce) {
//样例 https://ropsten.etherscan.io/tx/0x728a95b02beec3de9fb09ede00ca8ca6939bad2ad26c702a8392074dc04844c7
    byte[] addressAsBytes = Numeric.hexStringToByteArray(address);

    byte[] calculatedAddressAsBytes =
    Hash.sha3(RlpEncoder.encode(
        new RlpList(
            RlpString.create(addressAsBytes),
            RlpString.create((nonce)))));

    calculatedAddressAsBytes = Arrays.copyOfRange(calculatedAddressAsBytes,
        12, calculatedAddressAsBytes.length);
    String calculatedAddressAsHex = Numeric.toHexString(calculatedAddressAsBytes);
    return calculatedAddressAsHex;
}

public Stirng toolMethods () {
    
    // step 1: 获得web3j支持
    HttpService httpService = new HttpService("https://ropsten.infura.io/JOEnl84Gm76oX0RMUrJB");//节点地址视具体情况配置，这里是我申请的节点地址
    web3 = Web3j.build(httpService);
    Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();
    String clientVersion = web3ClientVersion.getWeb3ClientVersion();
    System.out.println(clientVersion);

    // step 2: 通过BIP39算法生成钱包（助记词、私钥、公钥、地址、keystore文件）
    String keyStoreDir = WalletUtils.getDefaultKeyDirectory();
    System.out.println("生成keyStore文件的默认目录：" + keyStoreDir);
    //通过密码及keystore目录生成钱包
    Bip39Wallet wallet = WalletUtils.generateBip39Wallet("yourpassword", new File(keyStoreDir));
    //keyStore文件名
    System.out.println(wallet.getFilename());
    //12个单词的助记词
    System.out.println(wallet.getMnemonic());

    // step 3-1: 通过密码与助记词获得钱包地址、公钥及私钥信息  
    Credentials credentials = WalletUtils.loadBip39Credentials("yourpassword",
       "cherry type collect echo derive shy balcony dog concert picture kid february");
     //钱包地址
    System.out.println(credentials.getAddress());
     //公钥16进制字符串表示
    System.out.println(credentials.getEcKeyPair().getPublicKey().toString(16));
     //私钥16进制字符串表示
    System.out.println(credentials.getEcKeyPair().getPrivateKey().toString(16));


    // step 3-2: 通过密码与keyStore文件获得钱包地址、公钥及私钥信息  
    credentials = WalletUtils.loadCredentials("yourpassword", keyStoreDir + "/UTC--2018-05-22T02-46-57.932000000Z--ae45f5aec6e6e7c0780a2a09dc830a9c3cb5b16b.json" );
    System.out.println(credentials.getAddress());
    System.out.println(credentials.getEcKeyPair().getPublicKey().toString(16));
    System.out.println(credentials.getEcKeyPair().getPrivateKey().toString(16));


    // step 4: 通过密码与keyStore文件进行转账操作 
    try {
       web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));  
         //通过密码和keystore文件获得钱包控制权
       Credentials credentials = WalletUtils.loadCredentials("aaaaaaaa", "/Users/yves/ethereum/privateChain/keystore/UTC--2018-05-22T11-48-03.459005936Z--0df14334e094acc0197d52a415d799c2b8a3b04b");
         //转账交易
       TransactionReceipt transferReceipt = WalletTransfer.sendFunds(
            web3, credentials,//web3指定网络、credentials指定转出钱包账户
            "0x18f54aade5dde6ce3772b78b293d76c25d874f92",  // 将以太币发送到此账户
            BigDecimal.ONE, Convert.Unit.ETHER)
       .send();
    }catch (Exception e){
       e.printStackTrace();
    }      

    // step 5: 通过账户地址查询余额
    web3 = Web3j.build(new HttpService("http://127.0.0.1:8545"));
    Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().sendAsync().get();

    EthGetBalance ethGetBalance = web3.ethGetBalance("0x0df14334e094acc0197d52a415d799c2b8a3b04b", DefaultBlockParameterName.LATEST)
                                        .sendAsync()
                                        .get();        

    BigInteger wei = ethGetBalance.getBalance();         
    System.out.println("balance is :" + wei);
}