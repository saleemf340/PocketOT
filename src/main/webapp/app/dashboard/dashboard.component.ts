import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterModule } from '@angular/router';
import SharedModule from 'app/shared/shared.module';
import { AccountService } from 'app/core/auth/account.service';
import { Account } from 'app/core/auth/account.model';

@Component({
  selector: 'jhi-dashboard',
  standalone: true,
  imports: [SharedModule, RouterModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss',
})
export default class DashboardComponent implements OnInit {
  account = signal<Account | null>(null);
  private readonly accountService = inject(AccountService);

  ngOnInit(): void {
    this.accountService.identity().subscribe(account => this.account.set(account));
  }
}
