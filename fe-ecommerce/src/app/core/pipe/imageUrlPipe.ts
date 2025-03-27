import { Pipe, PipeTransform } from '@angular/core';
import { environment } from 'environments/environment';

@Pipe({
  name: 'imageUrl',
  pure: true,
  standalone: true
})
export class ImageUrlPipe implements PipeTransform {
  transform(value: string): string {
    if (!value) {
      return 'assets/images/no-image.png';
    }

    return `${environment.imageUrl}${value}`;
  }
}
