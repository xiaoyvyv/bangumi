export interface LikeActionEntity {
    type: number;
    main_id: number;
    value: string;
    total: number;
    emoji: string;
    selected: boolean;
    users: {
        "username": string;
        "nickname": string;
    }[];
}