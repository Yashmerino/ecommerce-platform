/*
 * MIT License
 *
 * Copyright (c) 2023 Artiom Bozieac
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

import * as React from 'react';
import Link from '@mui/material/Link';
import Grid from '@mui/material/Grid';
import Container from '@mui/material/Container';
import { Link as RouterLink, useNavigate } from 'react-router-dom';
import { Box, Snackbar, Alert } from '@mui/material';

import UserInputFields from './UserInputFields';
import * as UserRequest from '../../../api/UserRequest';
import * as AuthRequest from '../../../api/AuthRequest';
import { InputError } from '../../../utils/InputErrorUtils';

import { useAppDispatch, useAppSelector } from '../../../hooks';
import { updateJwt } from '../../../slices/jwtSlice';
import { updateUsername } from '../../../slices/usernameSlice';
import { parseJwt } from '../../../utils/Utils';
import { updateInfo } from '../../../slices/infoSlice';
import { getTranslation } from '../../../../i18n/i18n';

const LoginPage = () => {
  const lang = useAppSelector(state => state.lang.lang);

  const [inputErrors, setInputErrors] = React.useState<InputError[]>([]);
  const [error, setError] = React.useState("");
  const navigate = useNavigate();

  const dispatch = useAppDispatch();

  const handleSubmit = async (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    setInputErrors([]);
    setError("");

    const data = new FormData(event.currentTarget);
    let username = data.get('username')?.toString();
    let password = data.get('password')?.toString();

    const response = await AuthRequest.login(username ?? "", password ?? "");
    if (response.error) {
      setError(response.error);
    } else if (response.accessToken) {
      dispatch(updateJwt(response.accessToken));
      dispatch(updateUsername(parseJwt(response.accessToken).sub));
      dispatch(updateInfo(await UserRequest.getUserInfo(username ?? "")));
      navigate("/products");
    } else {
      response.fieldErrors && setInputErrors(response.fieldErrors);
    }
  };

  const handleAlertClick = () => {
    setError("");
  };

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        bgcolor: 'background.default',
      }}
    >
      {error.length > 0 && (
        <Snackbar
          open={error.length > 0}
          autoHideDuration={2000}
          onClose={handleAlertClick}
          anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
        >
          <Alert
            data-testid="alert-error"
            onClose={handleAlertClick}
            severity="error"
            sx={{
              width: '100%',
              boxShadow: 2,
            }}
            id="alert-error"
          >
            {getTranslation(lang, error)}
          </Alert>
        </Snackbar>
      )}
      <Container
        maxWidth="sm"
        sx={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'center',
          py: 8,
        }}
      >
        <Box
          sx={{
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            position: 'relative',
          }}
        >
          <UserInputFields
            title={getTranslation(lang, "sign_in")}
            buttonText={getTranslation(lang, "sign_in")}
            handleSubmit={handleSubmit}
            isEmailAndRoleMandatory={false}
            inputErrors={inputErrors}
          />
          <Box
            sx={{
              mt: 3,
              textAlign: 'center',
            }}
          >
            <Link
              component={RouterLink}
              to="/register"
              variant="body1"
              id="dont-have-account">
              {getTranslation(lang, "create_account_message")}
            </Link>
          </Box>
        </Box>
      </Container>
    </Box>
  );
}

export default LoginPage;