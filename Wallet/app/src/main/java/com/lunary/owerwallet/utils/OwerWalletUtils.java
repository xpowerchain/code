package com.lunary.owerwallet.utils;

import com.lunary.owerwallet.model.MyWallet;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletFile;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.Cipher;

import static org.web3j.crypto.Hash.sha256;

/**
 * Created by Administrator on 2018/8/2.
 */
public class OwerWalletUtils extends WalletUtils {
    private static final SecureRandom secureRandom = SecureRandomUtils.secureRandom();

    public static Bip39Wallet generateBip39Wallet(String password, File destinationDirectory)
            throws CipherException, IOException {
        byte[] initialEntropy = new byte[16];
        secureRandom.nextBytes(initialEntropy);

        String mnemonic = OwerMnemonicUtils.generateMnemonic(initialEntropy);
        byte[] seed = OwerMnemonicUtils.generateSeed(mnemonic, password);
        ECKeyPair privateKey = ECKeyPair.create(sha256(seed));

        String walletFile = generateWalletFile(password, privateKey, destinationDirectory, false);

        return new Bip39Wallet(walletFile, mnemonic);
    }
    public static Credentials loadCredentials(String password, WalletFile walletFile) throws IOException, CipherException {
        return Credentials.create(MyWallet.decrypt(password, walletFile));
    }
}
