/*
 * MIT License
 *
 * Copyright (c) 2017 Choko (choko@curioswitch.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.curioswitch.common.server.framework.auth.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.linecorp.armeria.server.ServiceRequestContext;
import com.linecorp.armeria.server.http.auth.Authorizer;
import com.linecorp.armeria.server.http.auth.OAuth2Token;
import io.netty.util.AttributeKey;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;

public class FirebaseAuthorizer implements Authorizer<OAuth2Token> {

  public static final AttributeKey<FirebaseToken> FIREBASE_TOKEN =
      AttributeKey.valueOf(FirebaseAuthorizer.class, "FIREBASE_TOKEN");

  private final FirebaseAuth firebaseAuth;
  private final FirebaseAuthConfig config;

  @Inject
  public FirebaseAuthorizer(FirebaseAuth firebaseAuth, FirebaseAuthConfig config) {
    this.firebaseAuth = firebaseAuth;
    this.config = config;
  }

  @Override
  public CompletionStage<Boolean> authorize(ServiceRequestContext ctx, OAuth2Token data) {
    CompletableFuture<Boolean> result = new CompletableFuture<>();
    firebaseAuth
        .verifyIdToken(data.accessToken())
        .addOnFailureListener(ignored -> result.complete(false))
        .addOnSuccessListener(
            token -> {
              if (!token.isEmailVerified() && !config.isAllowUnverifiedEmail()) {
                result.complete(false);
                return;
              }
              ctx.attr(FIREBASE_TOKEN).set(token);
              result.complete(true);
            });
    return result;
  }
}
