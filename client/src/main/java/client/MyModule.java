/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client;

import client.scenes.AdminPanelCtrl;
import client.scenes.MultiplayerEstimationQuestionCtrl;
import client.scenes.HomeCtrl;
import client.scenes.MainCtrl;
import client.scenes.MultiplayerAnswerCtrl;
import client.scenes.MultiplayerGameCtrl;
import client.scenes.MultiplayerQuestionCtrl;
import client.scenes.MultiplayerResultsCtrl;
import client.scenes.RankingCtrl;
import client.scenes.SoloAnswerCtrl;
import client.scenes.SoloEstimationQuestionCtrl;
import client.scenes.SoloQuestionCtrl;
import client.scenes.SoloResultsCtrl;
import client.scenes.WaitingCtrl;
import client.utils.ServerUtils;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;

public class MyModule implements Module {

    @Override
    public void configure(Binder binder) {
        binder.bind(MainCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MultiplayerEstimationQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(HomeCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MultiplayerAnswerCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MultiplayerGameCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MultiplayerQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(MultiplayerResultsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(RankingCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SoloAnswerCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SoloQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SoloEstimationQuestionCtrl.class).in(Scopes.SINGLETON);
        binder.bind(SoloResultsCtrl.class).in(Scopes.SINGLETON);
        binder.bind(WaitingCtrl.class).in(Scopes.SINGLETON);
        binder.bind(ServerUtils.class).in(Scopes.SINGLETON);
        binder.bind(AdminPanelCtrl.class).in(Scopes.SINGLETON);
    }
}