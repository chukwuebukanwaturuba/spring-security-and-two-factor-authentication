import { Component } from '@angular/core';
import {Router} from "@angular/router";
import {RegisterRequest} from "../../models/register-request";
import {VerificationRequest} from "../../models/verification-request";
import {AuthenticationResponse} from "../../models/authentication-response";
import {AuthenticationService} from "../../services/authentication.service";

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent {
  registerRequest: RegisterRequest = {};
  authResponse: AuthenticationResponse = {};
  message = '';
  otpCode = '';

  constructor(
    private authService: AuthenticationService,
    private router: Router) {

  }
  registerUser(){
    this.message = '';
    this.authService.register(this.registerRequest)
      .subscribe({
        next: (response) => {
          if (response){
            this.authResponse = response;
          } else {
            //inform the user
            this.message = 'Account created successfully\nYou will be redirected to the Login page in 3 seconds';
            setTimeout(() => {
              this.router.navigate(['login']);
            }, 3000)
          }
        }
        });
  }

  verifyTfa(){
    this.message = '';
    const verifyRequest: VerificationRequest = {
      email: this.registerRequest.email,
      code: this.otpCode
    };
    this.authService.verifyCode(verifyRequest)
      .subscribe({
        next: (response) => {
          this.message = 'Account created successfully\nYou will be redirected to the Welcome page in 3 seconds';
          setTimeout(() => {
            localStorage.setItem('token', response.accessToken as string);
            this.router.navigate(['welcome']);
          }, 3000)
        }
      });
  }
}
