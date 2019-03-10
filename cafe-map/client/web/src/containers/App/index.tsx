/*
 * MIT License
 *
 * Copyright (c) 2019 Choko (choko@curioswitch.org)
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
 *
 */

import { injectReducer } from '@curiostack/base-web';

import CssBaseline from '@material-ui/core/CssBaseline';
import React from 'react';
import Helmet from 'react-helmet';
import { hot } from 'react-hot-loader/root';
import { InjectedIntlProps, injectIntl } from 'react-intl';
import { connect } from 'react-redux';
import { Route, Switch } from 'react-router-dom';
import { compose, Reducer } from 'redux';
import { reducer as formReducer } from 'redux-form/immutable';

import HomePage from '../HomePage/loader';
import NotFoundPage from '../NotFoundPage/Loadable';

import { DispatchProps, mapDispatchToProps } from './actions';
import messages from './messages';
import reducer from './reducer';
import mapStateToProps, { SelectedProps } from './selectors';

type Props = SelectedProps & DispatchProps & InjectedIntlProps;

class App extends React.PureComponent<Props> {
  public render() {
    const {
      intl: { formatMessage: _ },
    } = this.props;
    return (
      <>
        <Helmet
          defaultTitle={_(messages.defaultTitle)}
          titleTemplate={_(messages.titleTemplate)}
        />
        <CssBaseline />
        <Switch>
          <Route exact path="/" component={HomePage} />
          <Route component={NotFoundPage} />
        </Switch>
      </>
    );
  }
}

const withConnect = connect(
  mapStateToProps,
  mapDispatchToProps,
);
const withReducer = injectReducer({
  reducer: reducer as Reducer<any>,
  key: 'app',
});
const withFormReducer = injectReducer({ key: 'form', reducer: formReducer });

export default compose(
  injectIntl,
  withReducer,
  withFormReducer,
  withConnect,
  hot,
)(App);
