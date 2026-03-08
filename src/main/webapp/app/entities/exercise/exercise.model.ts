export interface IExercise {
  id: number;
  uuid?: string | null;
  name?: string | null;
  videoLink?: string | null;
}

export type NewExercise = Omit<IExercise, 'id'> & { id: null };
