/*
 * MIT License
 *
 * Copyright (c) 2018 Choko (choko@curioswitch.org)
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

import { RouterState } from 'connected-react-router';
import { createLocation } from 'history';
import { Record } from 'immutable';
import { Reducer, ReducersMapObject } from 'redux';
import { combineReducers } from 'redux-immutable';
import { Actions, ActionTypes } from './actions';
import { InjectableStore } from './store';

export interface RouterStateRecord extends Record<RouterState>, RouterState {}

export const routeInitialState: RouterStateRecord = Record<RouterState>({
  location: createLocation(window.location.pathname),
  action: 'POP',
})();

function createGlobalReducer(appReducer: Reducer<any>, initialState: any) {
  return (state: any, action: Actions) => {
    const processedState =
      action.type === ActionTypes.RESET_STATE ? initialState : state;
    return appReducer(processedState, action);
  };
}

export function createInitalReducer(
  identityReducers: ReducersMapObject,
  nonInjectedReducers: ReducersMapObject,
  initialState: any,
): Reducer<any> {
  return createGlobalReducer(
    combineReducers({
      ...identityReducers,
      ...nonInjectedReducers,
    }),
    initialState,
  );
}

export default function createReducer(
  store: InjectableStore<any>,
): Reducer<any> {
  return createGlobalReducer(
    combineReducers({
      ...store.identityReducers,
      ...store.injectedReducers,
      ...store.nonInjectedReducers,
    }),
    store.initialState,
  );
}
